#include <windows.h>
#include <stdio.h>
#include <assert.h>

#include <jni.h>

#if defined ALICE_SLOW_AND_STEADY
	#define FORCE_SOFTWARE_RENDERING_TEXT "-Dalice.forceSoftwareRendering=true"
	#define MAXIMUM_HEAP 512
#else
	#define FORCE_SOFTWARE_RENDERING_TEXT "-Dalice.forceSoftwareRendering=false"
	#define MAXIMUM_HEAP 768
#endif

static JavaVM* g_pJVM = NULL;

static size_t GetTotalPhysicalHeapInMegabytes() {
	MEMORYSTATUS sMemoryStatus;
	GlobalMemoryStatus( &sMemoryStatus );
	size_t nBytesPerMegabyte = 1024*1024;
	return sMemoryStatus.dwTotalPhys / nBytesPerMegabyte;
}

static void AddClasspath( char* vcClasspath, int n ) {
	WIN32_FIND_DATA sFindData;
	HANDLE hFile;

	strncat( vcClasspath, "lib\\alice.jar", n );

	hFile = FindFirstFile( "externalLib\\*.jar", &sFindData ); 
	if( hFile ) {
		do {
			strncat( vcClasspath, ";externalLib\\", n );
			strncat( vcClasspath, sFindData.cFileName, n );
		} while( FindNextFile( hFile, &sFindData ) );
		FindClose( hFile );
	}
}

typedef JNIIMPORT jint ( JNICALL * JNIPROC )( JavaVM **, void **, void * );

static DWORD WINAPI JavaThread( LPVOID lpParameter ) {
	LPSTR lpCmdLine = (LPSTR)lpParameter;

	HMODULE hJVM = LoadLibrary( "jvm.dll" );
	JNIPROC fJNI_CreateJavaVM = NULL;
	if( hJVM ) {
		TCHAR vcProcName[] = TEXT( "JNI_CreateJavaVM" );
	
		fJNI_CreateJavaVM = (JNIPROC)GetProcAddress( hJVM, vcProcName );
		if( !fJNI_CreateJavaVM ) {
			MessageBox( NULL, "Could not find method JNI_CreateJavaVM", "JavaThread", MB_OK );
			return -1;
		}
	} else {
		MessageBox( NULL, "Could not LoadLibrary jvm.dll", "JavaThread", MB_OK );
		return -1;
	}

	JavaVM* pJVM;
	JNIEnv* pEnv;
	if( fJNI_CreateJavaVM ) {

		const int n = 4096;
		char vcClasspathOption[ n ];
		strncpy( vcClasspathOption, "-Djava.class.path=", n );
		AddClasspath( vcClasspathOption, n );

		char vcMaxHeapBuffer[ 256 ];
		size_t nTotalPhysicalHeapInMegabytes = GetTotalPhysicalHeapInMegabytes();
		size_t nMaxHeapInMegabytes = min( max( nTotalPhysicalHeapInMegabytes, 256 ), MAXIMUM_HEAP );
		sprintf( vcMaxHeapBuffer, "-Xmx%dm", nMaxHeapInMegabytes );

		JavaVMInitArgs vm_args;
		const int nOptionCount = 7;
		JavaVMOption options[ nOptionCount ];
		options[ 0 ].optionString = "-Xincgc";
		options[ 0 ].extraInfo = NULL;
		options[ 1 ].optionString = "-Xms32m";
		options[ 1 ].extraInfo = NULL;
		options[ 2 ].optionString = vcMaxHeapBuffer;
		options[ 2 ].extraInfo = NULL;
		options[ 3 ].optionString = "-Dpython.home=jython";
		options[ 3 ].extraInfo = NULL;
		options[ 4 ].optionString = "-Djava.library.path=lib/win32;externalLib/win32";
		options[ 4 ].extraInfo = NULL;
		options[ 5 ].optionString = FORCE_SOFTWARE_RENDERING_TEXT;
		options[ 5 ].extraInfo = NULL;
		options[ 6 ].optionString = vcClasspathOption;
		options[ 6 ].extraInfo = NULL;
		vm_args.version = JNI_VERSION_1_2;
		vm_args.options = options;
		
		vm_args.nOptions = nOptionCount;
		vm_args.ignoreUnrecognized = JNI_TRUE;

		jint res = fJNI_CreateJavaVM( &pJVM, (void**)&pEnv, &vm_args );
		if( res < 0 ) {
			MessageBox( NULL, "Can't create Java VM", "JavaThread", MB_OK );
			return -1;
		} else {
			g_pJVM = pJVM;
		}
	} else {
		MessageBox( NULL, "Could not find method JNI_CreateJavaVM", "JavaThread", MB_OK );
		return -1;
	}

    jclass cls;
    cls = pEnv->FindClass( "edu/cmu/cs/stage3/alice/authoringtool/JAlice" );
    if (cls == 0) {
		MessageBox( NULL, "Could not find edu/cmu/cs/stage3/alice/authoringtool/JAlice class", "JavaThread", MB_OK );
		return -1;
    }
 
    jmethodID mid = pEnv->GetStaticMethodID( cls, "main", "([Ljava/lang/String;)V");
    if (mid == 0) {
		MessageBox( NULL, "Could not find main", "JavaThread", MB_OK );
		return -1;
    }

	jobjectArray args;
	if( lpCmdLine && strlen( lpCmdLine ) ) {
		jstring jstr = pEnv->NewStringUTF( lpCmdLine );
		if (jstr == 0) {
			MessageBox( NULL, "Out of memory", "JavaThread", MB_OK );
			return -1;
		}
		args = pEnv->NewObjectArray( 1, pEnv->FindClass( "java/lang/String"), jstr );
	} else {
		args = pEnv->NewObjectArray( 0, pEnv->FindClass( "java/lang/String"), NULL );
	}
	if (args == 0) {
		MessageBox( NULL, "Out of memory", "JavaThread", MB_OK );
		return -1;
	}

    pEnv->CallStaticVoidMethod( cls, mid, args );
    pJVM->DestroyJavaVM();
    FreeLibrary( hJVM );
	return S_OK;
}

typedef WINUSERAPI UINT ( WINAPI * GetWindowModuleFileNameAProc ) (IN HWND, OUT LPSTR, IN UINT);

int APIENTRY WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow ) {
	::SetErrorMode( SEM_FAILCRITICALERRORS | SEM_NOOPENFILEERRORBOX );

	if( SetCurrentDirectory( "Required" ) ) {
		const char* vcPathBonus = ".\\jre\\bin;.\\jre\\bin\\client;";
		size_t nOldPathLength = GetEnvironmentVariable( "PATH", NULL, 0 );
		size_t nBonusLength = strlen( vcPathBonus );

		char* vcPathBuffer = new char[ nOldPathLength+nBonusLength+1 ];
		strncpy( vcPathBuffer, vcPathBonus, nBonusLength );

		GetEnvironmentVariable( "PATH", vcPathBuffer+nBonusLength, (DWORD)nOldPathLength );
		SetEnvironmentVariable( "PATH", vcPathBuffer );

		delete [] vcPathBuffer;

		DWORD nThreadID;
		HANDLE hThread = CreateThread( NULL, 0, JavaThread, lpCmdLine, 0, &nThreadID );

		// Main message loop:
		MSG msg;
		while( GetMessage( &msg, NULL, 0, 0 ) ) {
			TranslateMessage(&msg);
			DispatchMessage(&msg);
		}
		return (int)msg.wParam;
	} else {
		HWND hBogusWnd = ::CreateWindow( "Button", "bogus", WS_OVERLAPPEDWINDOW, CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT, NULL, NULL, hInstance, NULL );
		char* vcZipFileText = NULL;
		if( hBogusWnd ) {
			const size_t N = 1024;
			char vcCurrentDirectory[ N ];
			strncpy( vcCurrentDirectory, "", N );
			::GetCurrentDirectory( N, vcCurrentDirectory );
			if( strlen( vcCurrentDirectory ) ) {
				char vcModuleFileName[ N ];
				strncpy( vcModuleFileName, "", N );
				HMODULE hUser32 = LoadLibrary( "user32.dll" );
				if( hUser32 ) {
					GetWindowModuleFileNameAProc f_GetWindowModuleFileNameA = (GetWindowModuleFileNameAProc)GetProcAddress( hUser32, TEXT("GetWindowModuleFileNameA") );
					if( f_GetWindowModuleFileNameA ) {
						f_GetWindowModuleFileNameA( hBogusWnd, vcModuleFileName, N );
						if( strlen( vcModuleFileName ) ) {
							if( strnicmp( vcCurrentDirectory, vcModuleFileName, strlen( vcCurrentDirectory ) ) != 0 ) {
								const char* vcFormattedString = "It appears that you are attempting to run Alice from within a zip file.   THIS WILL NOT WORK.  Alice depends on other files within the zip file which must all be extracted.\n\nIf you do not know how to extract files from a zip file, then Windows may have prompted you with a \"Compressed (zipped) Folders Warning\" with an \"Extract all\" button.  If so, you should be able to retry running Alice, press the \"Extract all\" button, and follow the instructions given.\n\n\nIf all else fails, please submit a bug report (http://www.alice.org/bugreport/submit.php) with the following debugging information:\nCurrent Directory: %s\nModule Filename: %s";
								size_t nLength = strlen( vcCurrentDirectory ) + strlen( vcModuleFileName ) + strlen( vcFormattedString );
								vcZipFileText = new char[ nLength ];
								sprintf( vcZipFileText, vcFormattedString, vcCurrentDirectory, vcModuleFileName );
							}
						}
					}
				}
			}
		}
		const char* vcCaption = "Unable to find Required directory";
		if( vcZipFileText ) {
			::MessageBox( NULL, vcZipFileText, vcCaption, MB_OK );
			delete [] vcZipFileText;
		} else {
			::MessageBox( NULL, "The Alice program expects a folder called \"Required\" to be next to it when Alice runs. Alice did not see that folder, and can not run.", vcCaption, MB_OK );
		}
		return 0;
	}
}
