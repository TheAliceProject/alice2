from edu.cmu.cs.stage3.alice.authoringtool import JAlice
from edu.cmu.cs.stage3.util import StringTypePair
from java.lang import Boolean
from java.lang import Double
from java.lang import Integer
from java.lang import String
from edu.cmu.cs.stage3.math import Vector3
from edu.cmu.cs.stage3.math import Matrix44
import edu
import java
import javax
import string

# HACK: until os.path works
def os_path_join( *args ):
	return string.join( args, java.io.File.separator )

####################################
# load common resource data
####################################

standardResourcesFile = os.path.join( JAlice.getAliceHomeDirectory(), "resources/common/StandardResources.py" )
execfile( standardResourcesFile.getAbsolutePath() )


##################
# Format Config
##################

formatMap = {
	edu.cmu.cs.stage3.alice.core.response.MoveAnimation : "<<<subject>>>.mover( <direction>, <amount> );",
	edu.cmu.cs.stage3.alice.core.response.MoveTowardAnimation : "<<<subject>>>.acercarseA( <target>, <amount> );",
	edu.cmu.cs.stage3.alice.core.response.MoveAwayFromAnimation : "<<<subject>>>.alejarseDe( <target>, <amount> );",
	edu.cmu.cs.stage3.alice.core.response.TurnAnimation : "<<<subject>>>.girar( <direction>, <amount> );",
	edu.cmu.cs.stage3.alice.core.response.RollAnimation : "<<<subject>>>.rodar( <direction>, <amount> );",
	edu.cmu.cs.stage3.alice.core.response.MoveAtSpeed : "<<<subject>>>.moverConVelocidad( <direction>, <speed> );",
	edu.cmu.cs.stage3.alice.core.response.TurnAtSpeed : "<<<subject>>>.girarConVelocidad( <direction>, <speed> );",
	edu.cmu.cs.stage3.alice.core.response.RollAtSpeed : "<<<subject>>>.rodarConVelocidad( <direction>, <speed> );",
	edu.cmu.cs.stage3.alice.core.response.ResizeAnimation : "<<<subject>>>.cambiarElTamaño( <amount> );",
	edu.cmu.cs.stage3.alice.core.response.PointAtAnimation : "<<<subject>>>.girarApuntarA( <target> );",
	edu.cmu.cs.stage3.alice.core.response.TurnToFaceAnimation : "<<<subject>>>.girarParaEncararA( <target> );",
	edu.cmu.cs.stage3.alice.core.response.TurnAwayFromAnimation : "<<<subject>>>.girarParaAlejarseDe( <target> );",
	edu.cmu.cs.stage3.alice.core.response.PointAtConstraint : "<<<subject>>>.restringidoParaApuntarA( <target> );",
	edu.cmu.cs.stage3.alice.core.response.GetAGoodLookAtAnimation : "<<<subject>>>.tomarUnaBuenaMirada( <target> );",
	edu.cmu.cs.stage3.alice.core.response.StandUpAnimation : "<<<subject>>>.pararse();",
	edu.cmu.cs.stage3.alice.core.response.PositionAnimation : "<<<subject>>>.moverA( <asSeenBy> );",
	edu.cmu.cs.stage3.alice.core.response.PlaceAnimation : "<<<subject>>>.colocar( <amount>, <spatialRelation>, <asSeenBy> );",
	edu.cmu.cs.stage3.alice.core.response.QuaternionAnimation : "<<<subject>>>.orientarA( <asSeenBy> );",
	edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation : "<<<subject>>>.establecerElPuntoDeVista( <asSeenBy> );",
	edu.cmu.cs.stage3.alice.core.response.PropertyAnimation : "<element>.establecer( <propertyName>, <value> );",
	edu.cmu.cs.stage3.alice.core.response.SoundResponse : "<<<subject>>>.reproducirSonido( <sound> );",
	edu.cmu.cs.stage3.alice.core.response.Wait : "esperar( <duration> );",
	edu.cmu.cs.stage3.alice.core.response.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.response.Print : "imprimir( <text>, <object> );",
	edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse : "<userDefinedResponse>( <requiredActualParameters> );",
	edu.cmu.cs.stage3.alice.core.response.ScriptResponse : "Guion (Script)( <script> );",
	edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse : "respuestaDefinidaPorElGuion( <script> );",
	edu.cmu.cs.stage3.alice.core.response.SayAnimation : "<<<subject>>>.dice( <what> );",
	edu.cmu.cs.stage3.alice.core.response.ThinkAnimation : "<<<subject>>>.piensa( <what> );",
	edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse : "runPositionKeyframeAnimationOn( <subject> );",
	edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse : "runOrientationKeyframeAnimationOn( <subject> );",
	edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse : "runScaleKeyframeAnimationOn( <subject> );",
	edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse : "runKeyframeAnimationOn( <subject> );",
	edu.cmu.cs.stage3.alice.core.response.PoseAnimation : "<<<subject>>>.establecerPose( <pose> );",
	edu.cmu.cs.stage3.alice.core.response.Increment : "<<<variable>>>++",
	edu.cmu.cs.stage3.alice.core.response.Decrement : "<<<variable>>>--",

	edu.cmu.cs.stage3.alice.core.response.VehiclePropertyAnimation : "<element>.establecer( <propertyName>, <value> );",

	edu.cmu.cs.stage3.alice.core.response.list.InsertItemAtBeginning : "<<<list>>>.insertar( 0, <item> );",
	edu.cmu.cs.stage3.alice.core.response.list.InsertItemAtEnd : "<<<list>>>.insertar( <item> );",
	edu.cmu.cs.stage3.alice.core.response.list.InsertItemAtIndex : "<<<list>>>.insertar( <index>, <item> );",
	edu.cmu.cs.stage3.alice.core.response.list.RemoveItemFromBeginning : "<<<list>>>.eliminar( 0 );",
	edu.cmu.cs.stage3.alice.core.response.list.RemoveItemFromEnd : "<<<list>>>.eliminarElUltimo();", 
	edu.cmu.cs.stage3.alice.core.response.list.RemoveItemFromIndex : "<<<list>>>.eliminar( <index> );",
	edu.cmu.cs.stage3.alice.core.response.list.Clear : "<<<list>>>.borrar();",

	edu.cmu.cs.stage3.alice.core.response.array.SetItemAtIndex : "<<<array>>>[<index>] = <item>;",

	edu.cmu.cs.stage3.alice.core.response.vector3.SetX : "<<<vector3>>>.establecerDistanciaALaDerecha( <value> )",
	edu.cmu.cs.stage3.alice.core.response.vector3.SetY : "<<<vector3>>>.establecerDistanciaHaciaArriba( <value> )",
	edu.cmu.cs.stage3.alice.core.response.vector3.SetZ : "<<<vector3>>>.establecerDistanciaAlFrente( <value> )",

	edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion : "<userDefinedQuestion>( <requiredActualParameters> )",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Return : "return <<value>>;",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Print : "imprimir( <text>, <object> );",
	edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment : "<element>.establecer( <propertyName>, <value> );",

	edu.cmu.cs.stage3.alice.core.question.PartKeyed : "<<<owner>>>.parteLlamada( <key> )",

	edu.cmu.cs.stage3.alice.core.question.Width : "<<<subject>>>.obtenerElAncho()",
	edu.cmu.cs.stage3.alice.core.question.Height : "<<<subject>>>.obtenerLaAltura()",
	edu.cmu.cs.stage3.alice.core.question.Depth : "<<<subject>>>.obtenerLaProfundidad()",
	edu.cmu.cs.stage3.alice.core.question.Quaternion : "<<<subject>>>.obtenerQuaternion()",
	edu.cmu.cs.stage3.alice.core.question.Position : "<<<subject>>>.obtenerLaPosición()",
	edu.cmu.cs.stage3.alice.core.question.PointOfView : "<<<subject>>>.obtenerElPuntoDeVista()",

	edu.cmu.cs.stage3.alice.core.question.Not : "!<a>",
	edu.cmu.cs.stage3.alice.core.question.And : "(<a>&&<b>)",
	edu.cmu.cs.stage3.alice.core.question.Or : "(<a>||<b>)",

	edu.cmu.cs.stage3.alice.core.question.StringConcatQuestion : "<a>+<b>",
	edu.cmu.cs.stage3.alice.core.question.ToStringQuestion : "<what>.toString()",

	edu.cmu.cs.stage3.alice.core.question.StringToUpperCaseQuestion : "<a>.toUpperCase",
	edu.cmu.cs.stage3.alice.core.question.StringToLowerCaseQuestion : "<a>.toLowerCase",

	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber : "DiálogoParaPedirNúmero(<question>)",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo : "DiálogoParaPedirBooleano(<question>)",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString : "DiálogoParaPedirDeSequeciaDeCaracters(<question>)",

	edu.cmu.cs.stage3.alice.core.question.IsEqualTo : "<a>==<b>",
	edu.cmu.cs.stage3.alice.core.question.IsNotEqualTo : "<a>!=<b>",

	edu.cmu.cs.stage3.alice.core.question.NumberIsEqualTo : "<a>==<b>",
	edu.cmu.cs.stage3.alice.core.question.NumberIsNotEqualTo : "<a>!=<b>",
	edu.cmu.cs.stage3.alice.core.question.NumberIsGreaterThan : "<a>><b>",
	edu.cmu.cs.stage3.alice.core.question.NumberIsGreaterThanOrEqualTo : "<a>>=<b>",
	edu.cmu.cs.stage3.alice.core.question.NumberIsLessThan : "<a>&lt;<b>",
	edu.cmu.cs.stage3.alice.core.question.NumberIsLessThanOrEqualTo : "<a>&lt;=<b>",

	edu.cmu.cs.stage3.alice.core.question.NumberAddition : "(<a>+<b>)", 
	edu.cmu.cs.stage3.alice.core.question.NumberSubtraction : "(<a>-<b>)", 
	edu.cmu.cs.stage3.alice.core.question.NumberMultiplication : "(<a>*<b>)", 
	edu.cmu.cs.stage3.alice.core.question.NumberDivision : "(<a>/<b>)",

	edu.cmu.cs.stage3.alice.core.question.math.Min : "Math.min( <a>, <b> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Max : "Math.max( <a>, <b> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Abs : "Math.abs( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Sqrt : "Math.sqrt( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Floor : "Math.floor( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Ceil : "Math.ceil( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Sin : "Math.sin( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Cos : "Math.cos( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Tan : "Math.tan( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.ASin : "Math.asin( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.ACos : "Math.acos( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.ATan : "Math.atan( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.ATan2 : "Math.atan2( <a>, <b> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Pow : "Math.pow( <a>, <b> )",
	edu.cmu.cs.stage3.alice.core.question.math.Log : "Math.natural log of <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Exp : "Math.exp( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.IEEERemainder : "Math.IEEERemainder( <a>, <b> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Int : "(int) <a>",
	edu.cmu.cs.stage3.alice.core.question.math.Round : "Math.round( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.ToDegrees : "Math.toDegrees( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.ToRadians : "Math.toRadians( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.SuperSqrt : "superSquareRoot( <a>, <b> )", 

	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromLeftEdge : "Ratón.obtenerDistanciaAlBordeIzquierdo()", 
	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromTopEdge : "Ratón.obtenerDistanciaAlBordeSuperior()", 

	edu.cmu.cs.stage3.alice.core.question.time.TimeElapsedSinceWorldStart : "obtenerElTiempoOcurridoDesdeQueComenzóElMundo()", 

	edu.cmu.cs.stage3.alice.core.question.time.Year : "obtenerAño()", 
	edu.cmu.cs.stage3.alice.core.question.time.MonthOfYear : "obtenerMesDelAño()", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfYear : "obtenerDiaDelAño()", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfMonth : "obtenerDiaDelMes()", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeek : "obtenerDiaDeLaSemana()", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeekInMonth : "obtenerDiaDeLaSemanaEnMes()", 
	edu.cmu.cs.stage3.alice.core.question.time.IsAM : "esAM()", 
	edu.cmu.cs.stage3.alice.core.question.time.IsPM : "esPM()", 
	edu.cmu.cs.stage3.alice.core.question.time.HourOfAMOrPM : "obtenerHoraDeAMóPM()", 
	edu.cmu.cs.stage3.alice.core.question.time.HourOfDay : "obtenerHoraDelDia()", 
	edu.cmu.cs.stage3.alice.core.question.time.MinuteOfHour : "obtenerHoraDelD¡a()", 
	edu.cmu.cs.stage3.alice.core.question.time.SecondOfMinute : "obtenerSegundoDelMinuto()", 

	edu.cmu.cs.stage3.alice.core.question.RandomBoolean : "Random.nextBoolean()",
	edu.cmu.cs.stage3.alice.core.question.RandomNumber : "Random.nextDouble()",

	edu.cmu.cs.stage3.alice.core.question.list.Contains : "<list>.contiene( <item> )",
	edu.cmu.cs.stage3.alice.core.question.list.FirstIndexOfItem : "<list>.¡ndiceDe( <item> )",
	edu.cmu.cs.stage3.alice.core.question.list.IsEmpty : "<list>.estaVacío()",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtBeginning : "<list>[0]",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtEnd : "<list>.obtenerUltimoElemento()",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex : "<list>[<index>]",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtRandomIndex : "<list>.obtenerElementoAlAzar()",
	edu.cmu.cs.stage3.alice.core.question.list.LastIndexOfItem : "<list>.UltimoIndiceDe( <item> )",
	edu.cmu.cs.stage3.alice.core.question.list.Size : "<list>.tamaño()",

	edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex : "<<<array>>>[<index>]",
	edu.cmu.cs.stage3.alice.core.question.array.Size : "<<<array>>>.tamaño",

	edu.cmu.cs.stage3.alice.core.question.IsAbove : "<<<subject>>>.estáArribaDe( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsBehind : "<<<subject>>>.estáDetrásDe( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsBelow : "<<<subject>>>.estáDebajoDe( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsInFrontOf : "<<<subject>>>.estáEnFrenteDe( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsLeftOf : "<<<subject>>>.estáALaIzquierdaDe( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsRightOf : "<<<subject>>>.estáALaDerechaDe( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsSmallerThan : "<<<subject>>>.esMásPequeñoQue( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsLargerThan : "<<<subject>>>.esMásGrandeQue( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsNarrowerThan : "<<<subject>>>.esMásEstrechoQue( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsWiderThan : "<<<subject>>>.esMásAnchoQue( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsShorterThan : "<<<subject>>>.esMásBajoQue( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsTallerThan : "<<<subject>>>.esMásAltoQue( <object> )",
 
	edu.cmu.cs.stage3.alice.core.question.IsCloseTo : "<<<subject>>>.estáCercaA( <threshold>, <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsFarFrom : "<<<subject>>>.estáLejosDe( <threshold>, <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceTo : "<<<subject>>>.distanciaA( <object> )",

	edu.cmu.cs.stage3.alice.core.question.DistanceToTheLeftOf : "<<<subject>>>.distanciaALaIzquierdaDe( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceToTheRightOf : "<<<subject>>>.distanciaALaDerechaDe ( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceAbove : "<<<subject>>>.distanciaArribaDe( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceBelow : "<<<subject>>>.distanciaDebajoDe( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceInFrontOf : "<<<subject>>>.distanciaEnFrenteDe( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceBehind : "<<<subject>>>.distancaDetrásDe( <object> )",

	edu.cmu.cs.stage3.alice.core.question.vector3.X : "<<<vector3>>>.obtenerDistanciaALaDerecha()",
	edu.cmu.cs.stage3.alice.core.question.vector3.Y : "<<<vector3>>>.obtenerDistanciaHaciaArriba()",
	edu.cmu.cs.stage3.alice.core.question.vector3.Z : "<<<vector3>>>.obtenerDistanciaAlFrente()",

	edu.cmu.cs.stage3.alice.core.question.PickQuestion : "quéSeRecogió()",

	edu.cmu.cs.stage3.alice.core.question.RightUpForward : "obtenerVector( <right>, <up>, <forward> )",

	edu.cmu.cs.stage3.alice.core.question.Pose : "<<<subject>>>.obtenerLaPose()",
}



##################
# Name Config
##################

nameMap = {
	"edu.cmu.cs.stage3.alice.core.response.DoInOrder" : "hacerEnOrden",
	"edu.cmu.cs.stage3.alice.core.response.DoTogether" : "hacerJuntos",
	"edu.cmu.cs.stage3.alice.core.response.IfElseInOrder" : "if",
	"edu.cmu.cs.stage3.alice.core.response.LoopNInOrder" : "lazo",
	"edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder" : "while",
	"edu.cmu.cs.stage3.alice.core.response.ForEachInOrder" : "paraTodosEnOrden",
	"edu.cmu.cs.stage3.alice.core.response.ForEachTogether" : "paraTodosJuntos",
	"edu.cmu.cs.stage3.alice.core.response.Print" : "imprimir",
	"edu.cmu.cs.stage3.alice.core.response.QuaternionAnimation.quaternion" : "compensado por",
	"edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation.pointOfView" : "punto de vista de",
	"edu.cmu.cs.stage3.alice.core.response.PositionAnimation.position" : "posición de",

	"edu.cmu.cs.stage3.alice.core.question.userdefined.Return" : "regresar",

	"edu.cmu.cs.stage3.alice.core.behavior.WorldStartBehavior" : "Cuando el Mundo comienza",
	"edu.cmu.cs.stage3.alice.core.behavior.WorldIsRunningBehavior" : "Cuando el Mundo se está ejecutando",
	"edu.cmu.cs.stage3.alice.core.behavior.KeyClickBehavior" : "Cuando se presiona <keyCode>",
	"edu.cmu.cs.stage3.alice.core.behavior.KeyIsPressedBehavior" : "Mientras <keyCode> es apretado",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseButtonClickBehavior" : "Cuando se hace clic con el <mouse> en <onWhat>",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseButtonIsPressedBehavior" : "Mientras el botón del ratón <mouse> se aprieta en <onWhat>",
	"edu.cmu.cs.stage3.alice.core.behavior.ConditionalBehavior" : "Mientras <condition> sea verdadero",
	"edu.cmu.cs.stage3.alice.core.behavior.ConditionalTriggerBehavior" : "Cuando <condition> se convierte en verdadero",
	"edu.cmu.cs.stage3.alice.core.behavior.VariableChangeBehavior" : "Cuando <variable> cambia",
	"edu.cmu.cs.stage3.alice.core.behavior.MessageReceivedBehavior" : "Cuando un mensaje es enviado de <fromWho> a <toWhom>", 
	"edu.cmu.cs.stage3.alice.core.behavior.DefaultMouseInteractionBehavior" : "Permitir que el <mouse> mueva <objects>",
	"edu.cmu.cs.stage3.alice.core.behavior.KeyboardNavigationBehavior" : "Permitir que <arrowKeys> mueva <subject>",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseNavigationBehavior" : "Permitir que el <mouse> mueva la cámara",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseLookingBehavior" : "Permitir que el <mouse> oriente la cámara",
	"edu.cmu.cs.stage3.alice.core.behavior.SoundMarkerPassedBehavior" : "Cuando el sonido <marker> se reproduce",
	"edu.cmu.cs.stage3.alice.core.behavior.SoundLevelBehavior" : "Cuando el nivel de grabación del sonido es >= <level>",

	"edu.cmu.cs.stage3.alice.core.Model.opacity" : "opacidad",
	"edu.cmu.cs.stage3.alice.core.Model.diffuseColorMap" : "textura de la piel",
	"diffuseColorMap" : "textura de la piel",
	"edu.cmu.cs.stage3.alice.core.Transformable.localTransformation" : "cuaternion",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseButtonClickBehavior.onWhat" : "encimaDeQué",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseButtonIsPressedBehavior.onWhat" : "encimaDeQué",
	"edu.cmu.cs.stage3.alice.core.question.IsCloseTo.threshold" : "está dentro de",
	"edu.cmu.cs.stage3.alice.core.question.IsFarFrom.threshold" : "es por lo menos",
	"edu.cmu.cs.stage3.alice.core.question.IsCloseTo.object" : "de",
	"edu.cmu.cs.stage3.alice.core.question.IsFarFrom.object" : "lejos de",

	"edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Renderer" : "DirectX 7",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.openglrenderer.Renderer" : "OpenGL",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.java3drenderer.Renderer" : "Java3D",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.Renderer" : "JOGL",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer.Renderer" : "Ninguno",

	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_AND_END_GENTLY : "COMENZAR_Y_TERMINAR_SUAVEMENTE",
	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY : "COMENZAR_SUAVEMENTE_Y_TERMINAR_ABRUPTAMENTE",
	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY : "COMENZAR_ABRUPTAMENTE_Y_TERMINAR_SUAVEMENTE",
	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_AND_END_ABRUPTLY : "COMENZAR_Y_TERMINAR_ABRUPTAMENTE",

	edu.cmu.cs.stage3.alice.core.Direction.LEFT : "IZQUIERDA",
	edu.cmu.cs.stage3.alice.core.Direction.RIGHT : "DERECHA",
	edu.cmu.cs.stage3.alice.core.Direction.UP : "ARRIBA",
	edu.cmu.cs.stage3.alice.core.Direction.DOWN : "ABAJO",
	edu.cmu.cs.stage3.alice.core.Direction.FORWARD : "ADELANTE",
	edu.cmu.cs.stage3.alice.core.Direction.BACKWARD : "ATRAS",

	edu.cmu.cs.stage3.alice.core.SpatialRelation.LEFT_OF : "A_LA_IZQUIERDA_DE",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF : "A_LA_DERECHA_DE",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.ABOVE : "ENCIMA",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BELOW : "DEBAJO",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF : "EN_FRENTE_DE",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND : "DETRAS_DE",

	edu.cmu.cs.stage3.alice.core.Dimension.ALL : "TODO",
	edu.cmu.cs.stage3.alice.core.Dimension.LEFT_TO_RIGHT : "DE_IZQUIERA_A_DERECHA",
	edu.cmu.cs.stage3.alice.core.Dimension.TOP_TO_BOTTOM : "DE_ARRIBA_A_ABAJO",
	edu.cmu.cs.stage3.alice.core.Dimension.FRONT_TO_BACK : "DE_ADELANTE_HACIA_ATRAS",

	edu.cmu.cs.stage3.alice.core.FogStyle.NONE : "NINGUNO",
	edu.cmu.cs.stage3.alice.core.FogStyle.LINEAR : "LINEAR",
	edu.cmu.cs.stage3.alice.core.FogStyle.EXPONENTIAL : "EXPONENCIAL",

	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.SOLID : "SOLIDO",
 	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.WIREFRAME : "ALAMBRE",
	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.POINTS : "PUNTOS",

	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.NONE : "NINGUNO",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.FLAT : "PLANO",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.SMOOTH : "SUAVE",

	Boolean.TRUE : "verdadero",
	Boolean.FALSE : "falso",

	edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE : "BLANCO",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK : "NEGRO",
	edu.cmu.cs.stage3.alice.scenegraph.Color.RED : "ROJO",
	edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN : "VERDE",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE : "AZUL",
	edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW : "AMARILLO",
	edu.cmu.cs.stage3.alice.scenegraph.Color.PURPLE : "MORADO",
	edu.cmu.cs.stage3.alice.scenegraph.Color.ORANGE : "ANARANJADO",
	edu.cmu.cs.stage3.alice.scenegraph.Color.PINK : "ROSADO",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BROWN : "MARRON",
	edu.cmu.cs.stage3.alice.scenegraph.Color.CYAN : "AGUAMARINA",
	edu.cmu.cs.stage3.alice.scenegraph.Color.MAGENTA : "PURPURA",
	edu.cmu.cs.stage3.alice.scenegraph.Color.GRAY : "GRIS",
	edu.cmu.cs.stage3.alice.scenegraph.Color.LIGHT_GRAY : "GRIS_CLARO",
	edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY : "GRIS_OSCURO",

	edu.cmu.cs.stage3.util.HowMuch.INSTANCE : "LA_INSTANCIA",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS : "LA_INSTANCIA_Y_LAS_PARTES",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS : "LA_INSTANCIA_Y_TODOS_LOS_DESCENDIENTES",
}

htmlNameMap = {
	"edu.cmu.cs.stage3.alice.core.Transformable" : "Objeto",
	"edu.cmu.cs.stage3.alice.core.Model" : "Objeto",
	"java.lang.Number" : "Número",
	"java.lang.Boolean" : "Booleano",
	"java.lang.String" : "Cadena de caracteres(String)",
	"edu.cmu.cs.stage3.alice.scenegraph.Color" : "Color",
	"edu.cmu.cs.stage3.alice.core.TextureMap" : "Textura",
	"edu.cmu.cs.stage3.alice.core.Sound" : "Sonido",
	"edu.cmu.cs.stage3.alice.core.Pose" : "Pose",
	"edu.cmu.cs.stage3.math.Vector3" : "Posición",
	"edu.cmu.cs.stage3.math.Quaternion" : "Orientación",
	"edu.cmu.cs.stage3.math.Matrix44" : "PuntoDeVista",
	"edu.cmu.cs.stage3.alice.core.ReferenceFrame" : "Objeto",
	"edu.cmu.cs.stage3.alice.core.Light" : "Luz",
	"edu.cmu.cs.stage3.alice.core.Direction" : "Dirección",
	"edu.cmu.cs.stage3.alice.core.Collection" : "[]",
}


####################
# Color Config
####################

colorMap = {
	"disabledHTMLText": java.awt.Color( 200, 200, 200 ),
	"disabledHTML": java.awt.Color( 230, 230, 230 ),
	"DoInOrder" : java.awt.Color( 255, 255, 255 ),
	"DoTogether" : java.awt.Color( 255, 255, 255 ),
	"IfElseInOrder" : java.awt.Color( 255, 255, 255 ),
	"LoopNInOrder" : java.awt.Color( 255, 255, 255 ),
	"WhileLoopInOrder" : java.awt.Color( 255, 255, 255 ),
	"ForEach" : java.awt.Color( 255, 255, 255 ),
	"ForEachInOrder" : java.awt.Color( 255, 255, 255 ),
	"ForAllTogether" : java.awt.Color( 255, 255, 255 ),
	"Wait" : java.awt.Color( 255, 255, 255 ),
	"ScriptResponse" : java.awt.Color( 255, 255, 255 ),
	"ScriptDefinedResponse" : java.awt.Color( 255, 255, 255 ),
	"Print" : java.awt.Color( 255, 255, 255 ),
	"Comment" : java.awt.Color( 255, 255, 255 ),
	"Return" : java.awt.Color( 255, 255, 255 ),
	"PropertyAssignment" : java.awt.Color( 255, 255, 255 ),
	"accessibleMathTile" : java.awt.Color( 255, 255, 255 ),
	"dndHighlight" : java.awt.Color( 255, 255, 255 ),
	"dndHighlight2" : java.awt.Color( 0, 200, 0 ),
	"dndHighlight3" : java.awt.Color( 230, 0, 0 ),
	"propertyViewControllerBackground" : java.awt.Color( 255, 255, 255 ),
	"objectTreeSelected" : java.awt.Color( 96, 32, 200 ),
	"objectTreeBackground" : java.awt.Color( 240, 233, 207 ),
	"objectTreeDisabled" : java.awt.Color( 220, 220, 220 ),
	"objectTreeText" : java.awt.Color( 0, 0, 0 ),
	"objectTreeDisabledText" : java.awt.Color( 150, 150, 150 ),
	"objectTreeSelectedText" : java.awt.Color( 240, 240, 240 ),
	"guiEffectsDisabledBackground" : java.awt.Color( 245, 245, 245, 100 ),
	"guiEffectsDisabledLine" : java.awt.Color( 128, 128, 128, 120 ),
	"guiEffectsShadow" : java.awt.Color( 0, 0, 0, 96 ),
	"guiEffectsEdge" : java.awt.Color( 0, 0, 0, 0 ),
	"guiEffectsTroughHighlight" : java.awt.Color( 255, 255, 255 ),
	"guiEffectsTroughShadow" : java.awt.Color( 96, 96, 96 ),
	"propertyDnDPanel" : java.awt.Color( 255, 255, 200 ),
	"prototypeParameter" : java.awt.Color( 255, 255, 255 ),
	"elementDnDPanel" : java.awt.Color( 255, 230, 180 ),
	"elementPrototypeDnDPanel" : java.awt.Color( 255, 255, 255 ),
	"formattedElementViewController" : java.awt.Color( 255, 255, 255 ),
	"response" : java.awt.Color( 255, 255, 255 ),
	"question" : java.awt.Color( 255, 255, 255 ),
	"userDefinedResponse" : java.awt.Color( 255, 255, 255 ),
	"userDefinedQuestion" : java.awt.Color( 255, 255, 255 ),
	"userDefinedQuestionComponent" : java.awt.Color( 255, 255, 255 ),
	"commentForeground" : java.awt.Color( 0, 164, 0 ),
	"variableDnDPanel" : java.awt.Color( 255, 255, 200 ),
	"userDefinedQuestionEditor" : java.awt.Color( 255, 255, 255 ),
	"userDefinedResponseEditor" : java.awt.Color( 255, 255, 255 ),
	"editorHeaderColor" : java.awt.Color( 255, 255, 255 ),
	"behavior" : java.awt.Color( 203, 231, 236 ),
	"behaviorBackground" : java.awt.Color( 255, 255, 255 ),
	"makeSceneEditorBigBackground" : java.awt.Color( 0, 150, 0 ),
	"makeSceneEditorSmallBackground" : java.awt.Color( 0, 150, 0 ),
	"stdErrTextColor" : java.awt.Color( 138, 212, 101 ),
        "mainFontColor" : java.awt.Color(0,0,0),
}


#########################
# Experimental Features
#########################

experimental = 0


#########################
# Misc
#########################

miscMap["javaLikeSyntax"] = "true"


####################################
# transfer resource data to Alice
####################################

resourceTransferFile = os.path.join( JAlice.getAliceHomeDirectory(), "resources/common/ResourceTransfer.py" )
execfile( resourceTransferFile.getAbsolutePath() )
