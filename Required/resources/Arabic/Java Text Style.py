# coding: utf-8
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

standardResourcesFile = os.path.join( JAlice.getAliceHomeDirectoryString(), "resources/common/StandardResources.py" )
execfile( standardResourcesFile)


##################
# Format Config
##################

formatMap = {
	edu.cmu.cs.stage3.alice.core.responses.MoveAnimation : "<<<subject>>>.تحرك( <direction>, <amount> );",
	edu.cmu.cs.stage3.alice.core.responses.MoveTowardAnimation : "<<<subject>>>.تحرك باتجاه( <target>, <amount> );",
	edu.cmu.cs.stage3.alice.core.responses.MoveAwayFromAnimation : "<<<subject>>>.تحرك بعيدًا عن( <target>, <amount> );",
	edu.cmu.cs.stage3.alice.core.responses.TurnAnimation : "<<<subject>>>.استدر( <direction>, <amount> );",
	edu.cmu.cs.stage3.alice.core.responses.RollAnimation : "<<<subject>>>.لف( <direction>, <amount> );",
	edu.cmu.cs.stage3.alice.core.responses.MoveAtSpeed : "<<<subject>>>.تحرك بسرعة( <direction>, <speed> );",
	edu.cmu.cs.stage3.alice.core.responses.TurnAtSpeed : "<<<subject>>>.استدر بسرعة( <direction>, <speed> );",
	edu.cmu.cs.stage3.alice.core.responses.RollAtSpeed : "<<<subject>>>.لف بسرعة( <direction>, <speed> );",
	edu.cmu.cs.stage3.alice.core.responses.ResizeAnimation : "<<<subject>>>.غير الحجم( <amount> );",
	edu.cmu.cs.stage3.alice.core.responses.PointAtAnimation : "<<<subject>>>.أشر إلى( <target> );",
	edu.cmu.cs.stage3.alice.core.responses.TurnToFaceAnimation : "<<<subject>>>.استدر لمواجهة( <target> );",
	edu.cmu.cs.stage3.alice.core.responses.TurnAwayFromAnimation : "<<<subject>>>.استدر بعيدًا عن( <target> );",
	edu.cmu.cs.stage3.alice.core.responses.PointAtConstraint : "<<<subject>>>.تقيّد بالإشارة إلى( <target> );",
	edu.cmu.cs.stage3.alice.core.responses.GetAGoodLookAtAnimation : "<<<subject>>>.انظر جيدًا إلى( <target> );",
	edu.cmu.cs.stage3.alice.core.responses.StandUpAnimation : "<<<subject>>>.قف();",
	edu.cmu.cs.stage3.alice.core.responses.PositionAnimation : "<<<subject>>>.تحرك إلى( <asSeenBy> );",
	edu.cmu.cs.stage3.alice.core.responses.PlaceAnimation : "<<<subject>>>.ضع( <amount>, <spatialRelation>, <asSeenBy> );",
	edu.cmu.cs.stage3.alice.core.responses.QuaternionAnimation : "<<<subject>>>.توجه إلى( <asSeenBy> );",
	edu.cmu.cs.stage3.alice.core.responses.PointOfViewAnimation : "<<<subject>>>.ضبط زاوية النظر( <asSeenBy> );",
	edu.cmu.cs.stage3.alice.core.responses.PropertyAnimation : "<element>.ضبط( <propertyName>, <value> );",
	edu.cmu.cs.stage3.alice.core.responses.SoundResponse : "<<<subject>>>.شغل الصوت( <sound> );",
	edu.cmu.cs.stage3.alice.core.responses.Wait : "انتظر( <duration> );",
	edu.cmu.cs.stage3.alice.core.responses.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.responses.Print : "اطبع( <text>, <object> );",
	edu.cmu.cs.stage3.alice.core.responses.CallToUserDefinedResponse : "<userDefinedResponse>( <requiredActualParameters> );",
	edu.cmu.cs.stage3.alice.core.responses.ScriptResponse : "نص( <script> );",
	edu.cmu.cs.stage3.alice.core.responses.ScriptDefinedResponse : " تحديد نص الرد ( <script> );",
	edu.cmu.cs.stage3.alice.core.responses.SayAnimation : "<<<subject>>>.قل( <what> );",
	edu.cmu.cs.stage3.alice.core.responses.ThinkAnimation : "<<<subject>>>.فكّر( <what> );",
	edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse : " تشغيل إطار الرسوم المتحركة الرئيس على وضع ( <subject> );",
	edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse : " تشغيل إطار الرسوم المتحركة الرئيس على توجه ( <subject> );",
	edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse : " تشغيل إطار الرسوم المتحركة الرئيس على نطاق ( <subject> );",
	edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse : " إطار الرسوم المتحركة الرئيس على ( <subject> );",
	edu.cmu.cs.stage3.alice.core.responses.PoseAnimation : "<<<subject>>>.تحديد الوضع( <pose> );",
	edu.cmu.cs.stage3.alice.core.responses.Increment : "<<<variable>>>++",
	edu.cmu.cs.stage3.alice.core.responses.Decrement : "<<<variable>>>--",

	edu.cmu.cs.stage3.alice.core.responses.VehiclePropertyAnimation : "<element>.تحديد( <propertyName>, <value> );",

	edu.cmu.cs.stage3.alice.core.responses.list.InsertItemAtBeginning : "<<<list>>>.أضف( 0, <item> );",
	edu.cmu.cs.stage3.alice.core.responses.list.InsertItemAtEnd : "<<<list>>>.أضف( <item> );",
	edu.cmu.cs.stage3.alice.core.responses.list.InsertItemAtIndex : "<<<list>>>.أضف( <index>, <item> );",
	edu.cmu.cs.stage3.alice.core.responses.list.RemoveItemFromBeginning : "<<<list>>>.أزل( 0 );",
	edu.cmu.cs.stage3.alice.core.responses.list.RemoveItemFromEnd : "<<<list>>>.أزل الأخير();",
	edu.cmu.cs.stage3.alice.core.responses.list.RemoveItemFromIndex : "<<<list>>>.أزل( <index> );",
	edu.cmu.cs.stage3.alice.core.responses.list.Clear : "<<<list>>>.امسح();",

	edu.cmu.cs.stage3.alice.core.responses.array.SetItemAtIndex : "<<<array>>>[<index>] = <item>;",

	edu.cmu.cs.stage3.alice.core.responses.vector3.SetX : "<<<vector3>>>.ضبط المسافة إلى اليمين( <value> )",
	edu.cmu.cs.stage3.alice.core.responses.vector3.SetY : "<<<vector3>>>.ضبط المسافة إلى الأعلى( <value> )",
	edu.cmu.cs.stage3.alice.core.responses.vector3.SetZ : "<<<vector3>>>.ضبط المسافة إلى الأمام( <value> )",

	edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion : "<userDefinedQuestion>( <requiredActualParameters> )",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Return : "إرجاع <<value>>;",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Print : "اطبع( <text>, <object> );",
	edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment : "<element>.تحديد( <propertyName>, <value> );",

	edu.cmu.cs.stage3.alice.core.question.PartKeyed : "<<<owner>>>.اسم الجزء( <key> )",

	edu.cmu.cs.stage3.alice.core.question.Width : "<<<subject>>>.الحصول على العرض()",
	edu.cmu.cs.stage3.alice.core.question.Height : "<<<subject>>>.الحصول على الطول()",
	edu.cmu.cs.stage3.alice.core.question.Depth : "<<<subject>>>.الحصول على العمق()",
	edu.cmu.cs.stage3.alice.core.question.Quaternion : "<<<subject>>>.الحصول على التربيع()",
	edu.cmu.cs.stage3.alice.core.question.Position : "<<<subject>>>.الحصول على الموضع()",
	edu.cmu.cs.stage3.alice.core.question.PointOfView : "<<<subject>>>.الحصول على زاوية النظر()",

	edu.cmu.cs.stage3.alice.core.question.Not : "!<a>",
	edu.cmu.cs.stage3.alice.core.question.And : "(<a>&&<b>)",
	edu.cmu.cs.stage3.alice.core.question.Or : "(<a>||<b>)",

	edu.cmu.cs.stage3.alice.core.question.StringConcatQuestion : "<a>+<b>",
	edu.cmu.cs.stage3.alice.core.question.ToStringQuestion : "<what>.إلى سلسلة نصية()",


	edu.cmu.cs.stage3.alice.core.question.StringToUpperCaseQuestion : "<a>.toUpperCase",
	edu.cmu.cs.stage3.alice.core.question.StringToLowerCaseQuestion : "<a>.toLowerCase",

	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber : "مربع الحوار للعدد(<question>)",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo : "مربع الحوار للقيمة المنطقية(<question>)",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString : "مربع الحوار للسلسلة النصية(<question>)",

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

	edu.cmu.cs.stage3.alice.core.question.math.Min : " العدد الأصغر بين ( <a>, <b> )",
	edu.cmu.cs.stage3.alice.core.question.math.Max : " العدد الأكبر بين ( <a>, <b> )",
	edu.cmu.cs.stage3.alice.core.question.math.Abs : " القيمة المطلقة( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.Sqrt : " الجذر التربيعي ( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.Floor : " الحد الأدنى ( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.Ceil : " الحد الأعلى ( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.Sin : " جيب الزاوية ( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.Cos : " جيب تمام الزاوية ( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.Tan : " مماس الزاوية ( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.ASin : " قوس جيب الزاوية ( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.ACos : " قوس جيب التمام ( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.ATan : " قوس المماس( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.ATan2 : " قوس المماس2 ( <a>, <b> )",
	edu.cmu.cs.stage3.alice.core.question.math.Pow : " مرفوعه إلى القوة ( <a>, <b> )",
	edu.cmu.cs.stage3.alice.core.question.math.Log : " اللوغاريتم الطبيعي ل<a>)",
	edu.cmu.cs.stage3.alice.core.question.math.Exp : " الأس( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.IEEERemainder : "IEEE الباقي من ( <a>, <b> )",
	edu.cmu.cs.stage3.alice.core.question.math.Int : "( عدد صحيح) <a>",
	edu.cmu.cs.stage3.alice.core.question.math.Round : " حول( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.ToDegrees : " تحويل من راديان إلى درجات ( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.ToRadians : " تحويل من درجات إلى راديان ( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.SuperSqrt : " الجذر ( <a>, <b> )",

	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromLeftEdge : " مسافة الفأرة من الحافة اليسرى ()",
	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromTopEdge : " مسافة الفأرة من الحافة العلوية ()",

	edu.cmu.cs.stage3.alice.core.question.time.TimeElapsedSinceWorldStart : " احصل على الوقت المنقضي منذ بداية العالم ()",

	edu.cmu.cs.stage3.alice.core.question.time.Year : " احصل على السنة ()",
	edu.cmu.cs.stage3.alice.core.question.time.MonthOfYear : " احصل على شهر من السنة ()",
	edu.cmu.cs.stage3.alice.core.question.time.DayOfYear : " احصل على يوم من السنة ()",
	edu.cmu.cs.stage3.alice.core.question.time.DayOfMonth : " احصل على يوم من الشهر ()",
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeek : " احصل على يوم من الأسبوع ()",
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeekInMonth : " احصل على يوم من الأسبوع في الشهر ()",
	edu.cmu.cs.stage3.alice.core.question.time.IsAM : " هل صباحًا()",
	edu.cmu.cs.stage3.alice.core.question.time.IsPM : " هل مساءًا()",
	edu.cmu.cs.stage3.alice.core.question.time.HourOfAMOrPM : " احصل على ساعة صباحًا أو مساءًا ()",
	edu.cmu.cs.stage3.alice.core.question.time.HourOfDay : " احصل على ساعة من اليوم ()",
	edu.cmu.cs.stage3.alice.core.question.time.MinuteOfHour : " احصل على دقيقة من الساعة ()",
	edu.cmu.cs.stage3.alice.core.question.time.SecondOfMinute : " احصل على ثانية من الدقيقة ()",

	edu.cmu.cs.stage3.alice.core.question.RandomBoolean : " القيمة المنطقية العشوائية التالية ()",
	edu.cmu.cs.stage3.alice.core.question.RandomNumber : " القيمة المزدوجة العشوائية التالية ()",

	edu.cmu.cs.stage3.alice.core.question.list.Contains : "<list>. تحتوي ( <item> )",
	edu.cmu.cs.stage3.alice.core.question.list.FirstIndexOfItem : "<list>. الرقم التسلسلي ( <item> )",
	edu.cmu.cs.stage3.alice.core.question.list.IsEmpty : "<list>. هل فارغة ()",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtBeginning : "<list>[0]",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtEnd : "<list>. احصل على اخر عنصر()",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex : "<list>[<index>]",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtRandomIndex : "<list>. احصل على عنصر عشوائي()",
	edu.cmu.cs.stage3.alice.core.question.list.LastIndexOfItem : "<list>. اخر عنصر من( <item> )",
	edu.cmu.cs.stage3.alice.core.question.list.Size : "<list>. حجم()",

	edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex : "<<<array>>>[<index>]",
	edu.cmu.cs.stage3.alice.core.question.array.Size : "<<<array>>>. طول",

	edu.cmu.cs.stage3.alice.core.question.IsAbove : "<<<subject>>>. هل أعلى ( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsBehind : "<<<subject>>>. هل وراء ( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsBelow : "<<<subject>>>. هل أسفل ( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsInFrontOf : "<<<subject>>>. هل أمام( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsLeftOf : "<<<subject>>>. هل على يسار ( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsRightOf : "<<<subject>>>. هل على يمين  ( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsSmallerThan : "<<<subject>>>. هل أصغر من ( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsLargerThan : "<<<subject>>>. هل أكبر من ( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsNarrowerThan : "<<<subject>>>. هل أضيق من ( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsWiderThan : "<<<subject>>>. هل أوسع من ( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsShorterThan : "<<<subject>>>. هل أقصر من ( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsTallerThan : "<<<subject>>>. هل أطول من ( <object> )",

	edu.cmu.cs.stage3.alice.core.question.IsCloseTo : "<<<subject>>>. هل قريب إلى ( <threshold>, <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsFarFrom : "<<<subject>>>. هل بعيد عن ( <threshold>, <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceTo : "<<<subject>>>. المسافة إلى ( <object> )",

	edu.cmu.cs.stage3.alice.core.question.DistanceToTheLeftOf : "<<<subject>>>. المسافة إلى اليسار من( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceToTheRightOf : "<<<subject>>>. المسافة إلى اليمين من( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceAbove : "<<<subject>>>. المسافة أعلى ( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceBelow : "<<<subject>>>. المسافة اسفل( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceInFrontOf : "<<<subject>>>. المسافة أمام( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceBehind : "<<<subject>>>. المسافة خلف ( <object> )",

	edu.cmu.cs.stage3.alice.core.question.vector3.X : "<<<vector3>>>. احصل على المسافة لليمين ()",
	edu.cmu.cs.stage3.alice.core.question.vector3.Y : "<<<vector3>>>. احصل على المسافة للأعلى ()",
	edu.cmu.cs.stage3.alice.core.question.vector3.Z : "<<<vector3>>>. احصل على المسافة للأمام ()",

	edu.cmu.cs.stage3.alice.core.question.PickQuestion : "ما الذي تم اختياره ()",

	edu.cmu.cs.stage3.alice.core.question.RightUpForward : " احصل على المتجه ( <right>, <up>, <forward> )",

	edu.cmu.cs.stage3.alice.core.question.Pose : "<<<subject>>>. احصل على الوضع الحالي ()",
}



##################
# Name Config
##################

nameMap = {
	"edu.cmu.cs.stage3.alice.core.responses.DoInOrder" : "افعل بالتتابع",
	"edu.cmu.cs.stage3.alice.core.responses.DoTogether" : "افعل بالتزامن",
	"edu.cmu.cs.stage3.alice.core.responses.IfElseInOrder" : "إذا",
	"edu.cmu.cs.stage3.alice.core.responses.LoopNInOrder" : "حلقة تكرار",
	"edu.cmu.cs.stage3.alice.core.responses.WhileLoopInOrder" : "بينما",
	"edu.cmu.cs.stage3.alice.core.responses.ForEachInOrder" : "للجميع بالتتابع",
	"edu.cmu.cs.stage3.alice.core.responses.ForEachTogether" : "للجميع بالتزامن",
	"edu.cmu.cs.stage3.alice.core.responses.Print" : "اطبع",
	"edu.cmu.cs.stage3.alice.core.responses.QuaternionAnimation.quaternion" : "توجيه ل",
	"edu.cmu.cs.stage3.alice.core.responses.PointOfViewAnimation.pointOfView" : "زاوية النظر ل",
	"edu.cmu.cs.stage3.alice.core.responses.PositionAnimation.position" : "موضع ال",

	"edu.cmu.cs.stage3.alice.core.question.userdefined.Return" : "إرجاع",

	"edu.cmu.cs.stage3.alice.core.behaviors.WorldStartBehavior" : "عندما يبدأ العالم",
	"edu.cmu.cs.stage3.alice.core.behaviors.WorldIsRunningBehavior" : "أثناء تشغيل العالم",
	"edu.cmu.cs.stage3.alice.core.behaviors.KeyClickBehavior" : "عندما <keyCode> يطبع",
	"edu.cmu.cs.stage3.alice.core.behaviors.KeyIsPressedBehavior" : "بينما <keyCode> يُضغط ",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonClickBehavior" : "عندما <mouse>على  يضغط <onWhat>",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonIsPressedBehavior" : "بينما <mouse> يضغط على <onWhat>",
	"edu.cmu.cs.stage3.alice.core.behaviors.ConditionalBehavior" : "بينما <condition> صحيح",
	"edu.cmu.cs.stage3.alice.core.behaviors.ConditionalTriggerBehavior" : "عندما <condition> يصبح صحيح",
	"edu.cmu.cs.stage3.alice.core.behaviors.VariableChangeBehavior" : "عندما <variable> يتغير",
	"edu.cmu.cs.stage3.alice.core.behaviors.MessageReceivedBehavior" : " عند تلقي رسالة بواسطة <toWhom> من <fromWho>",
	"edu.cmu.cs.stage3.alice.core.behaviors.DefaultMouseInteractionBehavior" : "دع <mouse> يتحرك <objects>",
	"edu.cmu.cs.stage3.alice.core.behaviors.KeyboardNavigationBehavior" : "دع <arrowKeys> يتحرك <subject>",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseNavigationBehavior" : "دع <mouse> يحرك الكاميرا",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseLookingBehavior" : "دع <mouse> يوجّه الكاميرا",
	"edu.cmu.cs.stage3.alice.core.behaviors.SoundMarkerPassedBehavior" : " عندما علامة الصوت  <marker> يشتغل",
	"edu.cmu.cs.stage3.alice.core.behaviors.SoundLevelBehavior" : "عندما عندما يكون مستوى تسجيل الصوت >= <level>",

	"edu.cmu.cs.stage3.alice.core.Model.opacity" : "عتامة",
	"edu.cmu.cs.stage3.alice.core.Model.diffuseColorMap" : "نسيج الجلد",
	"diffuseColorMap" : "نسيج الجلد",
	"edu.cmu.cs.stage3.alice.core.Transformable.localTransformation" : "زاوية النظر",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonClickBehavior.onWhat" : "على ماذا",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonIsPressedBehavior.onWhat" : "على ماذا",
	"edu.cmu.cs.stage3.alice.core.question.IsCloseTo.threshold" : "ضمن",
	"edu.cmu.cs.stage3.alice.core.question.IsFarFrom.threshold" : "على الأقل",
	"edu.cmu.cs.stage3.alice.core.question.IsCloseTo.object" : "من",
	"edu.cmu.cs.stage3.alice.core.question.IsFarFrom.object" : "بعيدًا عن",

	"edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Renderer" : "DirectX 7",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.openglrenderer.Renderer" : "OpenGL",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.java3drenderer.Renderer" : "Java3D",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.Renderer" : "JOGL",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer.Renderer" : "لاشيء",

	edu.cmu.cs.stage3.alice.core.styles.TraditionalAnimationStyle.BEGIN_AND_END_GENTLY : "إبدأ وانهي رويدًا",
	edu.cmu.cs.stage3.alice.core.styles.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY : " إبدأ رويدًا وانهي فجأة",
	edu.cmu.cs.stage3.alice.core.styles.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY : " إبدأ فجأة وانهي رويدًا ",
	edu.cmu.cs.stage3.alice.core.styles.TraditionalAnimationStyle.BEGIN_AND_END_ABRUPTLY : " إبدأ وانهي فجأة ",

	edu.cmu.cs.stage3.alice.core.Direction.LEFT : "يسار",
	edu.cmu.cs.stage3.alice.core.Direction.RIGHT : "يمين",
	edu.cmu.cs.stage3.alice.core.Direction.UP : "أعلى",
	edu.cmu.cs.stage3.alice.core.Direction.DOWN : "أسفل",
	edu.cmu.cs.stage3.alice.core.Direction.FORWARD : "أمام",
	edu.cmu.cs.stage3.alice.core.Direction.BACKWARD : "خلف",

	edu.cmu.cs.stage3.alice.core.SpatialRelation.LEFT_OF : "من اليسار",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF : "من اليمين",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.ABOVE : "أعلى",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BELOW : "أسفل",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF : "من أمام",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND : "وراء",

	edu.cmu.cs.stage3.alice.core.Dimension.ALL : "جميع",
	edu.cmu.cs.stage3.alice.core.Dimension.LEFT_TO_RIGHT : "من اليسار إلى اليمين",
	edu.cmu.cs.stage3.alice.core.Dimension.TOP_TO_BOTTOM : "من الأعلى إلى الأسفل",
	edu.cmu.cs.stage3.alice.core.Dimension.FRONT_TO_BACK : "من الأمام إلى الخلف",

	edu.cmu.cs.stage3.alice.core.FogStyle.NONE : "لاشيء",
	edu.cmu.cs.stage3.alice.core.FogStyle.LINEAR : "خطي",
	edu.cmu.cs.stage3.alice.core.FogStyle.EXPONENTIAL : "أسية",

	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.SOLID : "صلب",
	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.WIREFRAME : "إطار سلكي",
	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.POINTS : "نقاط",

	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.NONE : "لاشيء",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.FLAT : "مسطح",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.SMOOTH : "أملس",

	Boolean.TRUE : "صح",
	Boolean.FALSE : "خطأ",

	edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE : "أبيض",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK : "أسود",
	edu.cmu.cs.stage3.alice.scenegraph.Color.RED : "أحمر",
	edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN : "أخضر",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE : "أزرق",
	edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW : "أصفر",
	edu.cmu.cs.stage3.alice.scenegraph.Color.PURPLE : "بنفسجي",
	edu.cmu.cs.stage3.alice.scenegraph.Color.ORANGE : "برتقالي",
	edu.cmu.cs.stage3.alice.scenegraph.Color.PINK : "وردي",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BROWN : "بني",
	edu.cmu.cs.stage3.alice.scenegraph.Color.CYAN : "سماوي",
	edu.cmu.cs.stage3.alice.scenegraph.Color.MAGENTA : "ارجواني",
	edu.cmu.cs.stage3.alice.scenegraph.Color.GRAY : "رمادي",
	edu.cmu.cs.stage3.alice.scenegraph.Color.LIGHT_GRAY : "رمادي فاتح",
	edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY : "رمادي داكن",

	edu.cmu.cs.stage3.util.HowMuch.INSTANCE : "نموذج",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS : "نموذج وأجزاء",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS : "نموذج وكافة فروعه",
}

htmlNameMap = {
	"edu.cmu.cs.stage3.alice.core.Transformable" : "كائن",
	"edu.cmu.cs.stage3.alice.core.Model" : "كائن",
	"java.lang.Number" : "رقم",
	"java.lang.Boolean" : "منطقي",
	"java.lang.String" : "سلسلة نصية",
	"edu.cmu.cs.stage3.alice.scenegraph.Color" : "لون",
	"edu.cmu.cs.stage3.alice.core.TextureMap" : "نسيج",
	"edu.cmu.cs.stage3.alice.core.Sound" : "صوت",
	"edu.cmu.cs.stage3.alice.core.Pose" : "وضع",
	"edu.cmu.cs.stage3.math.Vector3" : "موضع",
	"edu.cmu.cs.stage3.math.Quaternion" : "اتجاه",
	"edu.cmu.cs.stage3.math.Matrix44" : "زاوية النظر",
	"edu.cmu.cs.stage3.alice.core.ReferenceFrame" : "كائن",
	"edu.cmu.cs.stage3.alice.core.Light" : "ضوء",
	"edu.cmu.cs.stage3.alice.core.Direction" : "اتجاه",
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

resourceTransferFile = os.path.join( JAlice.getAliceHomeDirectoryString(), "resources/common/ResourceTransfer.py" )
execfile( resourceTransferFile)
