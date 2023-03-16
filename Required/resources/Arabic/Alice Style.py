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
	edu.cmu.cs.stage3.alice.core.responses.MoveAnimation : "<<<subject>>> تحرك <<direction>><<amount>>",
	edu.cmu.cs.stage3.alice.core.responses.MoveTowardAnimation : "<<<subject>>> تحرك <<amount>> باتجاه <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.MoveAwayFromAnimation : "<<<subject>>> تحرك <<amount>> بعيدًا عن <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnAnimation : "<<<subject>>> استدر <<direction>><<amount>>",
	edu.cmu.cs.stage3.alice.core.responses.RollAnimation : "<<<subject>>> لف <<direction>><<amount>>",
	edu.cmu.cs.stage3.alice.core.responses.MoveAtSpeed : "<<<subject>>> تحرك بسرعة <<direction>><<speed>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnAtSpeed : "<<<subject>>> استدر بسرعة <<direction>><<speed>>",
	edu.cmu.cs.stage3.alice.core.responses.RollAtSpeed : "<<<subject>>> لف بسرعة  <<direction>><<speed>>",
	edu.cmu.cs.stage3.alice.core.responses.ResizeAnimation : "<<<subject>>> غير الحجم <<amount>>",
	edu.cmu.cs.stage3.alice.core.responses.PointAtAnimation : "<<<subject>>> أشر إلى <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnToFaceAnimation : "<<<subject>>> استدر لمواجهة <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnAwayFromAnimation : "<<<subject>>> استدر بعيدًا عن <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.PointAtConstraint : "<<<subject>>> تقيّد بالإشارة إلى <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnToFaceConstraint : "<<<subject>>> تقيّد بمواجهة  <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnAwayFromConstraint : "<<<subject>>> تقيّد بمواجهة بعيدًا عن  <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.GetAGoodLookAtAnimation : "<<<subject>>> انظر جيدًا إلى <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.StandUpAnimation : "<<<subject>>> قف",
	edu.cmu.cs.stage3.alice.core.responses.PositionAnimation : "<<<subject>>> تحرك إلى <<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.responses.PlaceAnimation : "<<<subject>>> caitlin تحرك إلى <<amount>><<spatialRelation>><<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.responses.QuaternionAnimation : "<<<subject>>> توجّه إلى <<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.responses.PointOfViewAnimation : "<<<subject>>> ضبط زاوية النظر إلى <<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.responses.PropertyAnimation : "<<<element>>> ضبط <propertyName> إلى <<value>>",
	edu.cmu.cs.stage3.alice.core.responses.SoundResponse : "<<<subject>>> شغّل الصوت <<sound>>",
	edu.cmu.cs.stage3.alice.core.responses.Wait : "انتظر <<duration>>",
	edu.cmu.cs.stage3.alice.core.responses.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.responses.Print : "اطبع <<text>> <<object>>",
	edu.cmu.cs.stage3.alice.core.responses.CallToUserDefinedResponse : "<userDefinedResponse><requiredActualParameters>",
	edu.cmu.cs.stage3.alice.core.responses.ScriptResponse : "نص <<script>>",
	edu.cmu.cs.stage3.alice.core.responses.ScriptDefinedResponse : "تحديد نص الرد <<script>>",
	edu.cmu.cs.stage3.alice.core.responses.SayAnimation : "<<<subject>>> قل <<what>>",
	edu.cmu.cs.stage3.alice.core.responses.ThinkAnimation : "<<<subject>>> فكر <<what>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse : "وضع إطار الرسوم المتحركة الرئيس <<subject>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse : "توجيه إطار الرسوم المتحركة الرئيس <<subject>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse : "قياس إطار الرسوم المتحركة الرئيس <<subject>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse : "إطار الرسوم المتحركة الرئيس <<subject>>",
	edu.cmu.cs.stage3.alice.core.responses.PoseAnimation : "<<<subject>>> ضبط الوضعية <<pose>>",
	edu.cmu.cs.stage3.alice.core.responses.Increment : "زيادة <<<variable>>> بمقدار 1",
	edu.cmu.cs.stage3.alice.core.responses.Decrement : "نقصان <<<variable>>> بمقدار 1",
        
        edu.cmu.cs.stage3.alice.core.responses.VehiclePropertyAnimation : "<element> ضبط <propertyName> إلى <value>",

	edu.cmu.cs.stage3.alice.core.responses.list.InsertItemAtBeginning : " إدراج <item> في البداية <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.InsertItemAtEnd : "إدراج <item> في النهاية <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.InsertItemAtIndex : "إدراج <item> في موضع <index> من <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.RemoveItemFromBeginning : " إزالة عنصر من البداية <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.RemoveItemFromEnd : " إزالة عنصر من النهاية <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.RemoveItemFromIndex : " إزالة عنصر من الموضع <index> من <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.Clear : " إزالة جميع العناصر من <<<list>>>",

	edu.cmu.cs.stage3.alice.core.responses.array.SetItemAtIndex : "ضبط العنصر <index> إلى <item> في <<<array>>>",

	edu.cmu.cs.stage3.alice.core.responses.vector3.SetX : "ضبط <<<vector3>>>'s المسافة إلى اليمين <<value>>",
	edu.cmu.cs.stage3.alice.core.responses.vector3.SetY : "ضبط <<<vector3>>>'s المسافة إلى الأعلى <<value>>",
	edu.cmu.cs.stage3.alice.core.responses.vector3.SetZ : "ضبط <<<vector3>>>'s المسافة إلى الأمام <<value>>",

	edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion : "<userDefinedQuestion><requiredActualParameters>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Return : "إرجاع <<value>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Print : "طباعة <<text>> <<object>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment : "<element> ضبط <propertyName> إلى <value>",

	edu.cmu.cs.stage3.alice.core.question.PartKeyed : "<<<owner>>>'s اسم الجزء <key>",

	edu.cmu.cs.stage3.alice.core.question.Width : "<<<subject>>>'s عرض",
	edu.cmu.cs.stage3.alice.core.question.Height : "<<<subject>>>'s ارتفاع",
	edu.cmu.cs.stage3.alice.core.question.Depth : "<<<subject>>>'s عمق",
	edu.cmu.cs.stage3.alice.core.question.Quaternion : "<<<subject>>>'s تربيع",
	edu.cmu.cs.stage3.alice.core.question.Position : "<<<subject>>>'s موقع",
	edu.cmu.cs.stage3.alice.core.question.PointOfView : "<<<subject>>>'s زاوية النظر",

	edu.cmu.cs.stage3.alice.core.question.Not : "ليس <a>",
	edu.cmu.cs.stage3.alice.core.question.And : "كلا <a> و <b>",
	edu.cmu.cs.stage3.alice.core.question.Or : "إما <a> أو <b>, أو كلا",

	edu.cmu.cs.stage3.alice.core.question.StringConcatQuestion : "<a> متصلة مع<b>",
	edu.cmu.cs.stage3.alice.core.question.ToStringQuestion : "<what> كسلسلة نصية",

	edu.cmu.cs.stage3.alice.core.question.StringToUpperCaseQuestion : "<a> to uppercase",


	edu.cmu.cs.stage3.alice.core.question.StringToLowerCaseQuestion : "<a> to lowercase",

	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber : " اسأل المستخدم عن عدد <<question>>",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo : "اسأل المستخدم نعم أو كلا <<question>>",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString : "اسأل المستخدم عن سلسلة نصية <<question>>",

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

	edu.cmu.cs.stage3.alice.core.question.math.Min : "العدد الأصغر بين <a> و <b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Max : " العدد الأكبر بين <a> و <b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Abs : "القيمة المطلقة <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Sqrt : "الجذر التربيعي <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Floor : "الحد الأدنى <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Ceil : "الحد الأعلى <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Sin : "جيب الزاوية <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Cos : "جيب تمام الزاوية <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Tan : "مماس الزاوية <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ASin : "قوس جيب الزاوية<a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ACos : "قوس جيب التمام <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ATan : "قوس المماس <a>",
	edu.cmu.cs.stage3.alice.core.question.math.ATan2 : "قوس المماس2 <a><b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Pow : "<a> مرفوعه إلى القوة <b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Log : "اللوغاريتم الطبيعي ل<a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Exp : " مرفوعه إلى القوة <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.IEEERemainder : "IEEE الباقي من <a>/<b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Int : "عدد صحيح <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Round : "حول <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ToDegrees : "<a> تحويل من راديان إلى درجات", 
	edu.cmu.cs.stage3.alice.core.question.math.ToRadians : "<a> تحويل من درجات إلى راديان", 
	edu.cmu.cs.stage3.alice.core.question.math.SuperSqrt : "<a> ل <b>الجذر ال",

	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromLeftEdge : "مسافة الفأرة من الحافة اليسرى", 
	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromTopEdge : "مسافة الفأرة من الحافة العلوية", 

	edu.cmu.cs.stage3.alice.core.question.time.TimeElapsedSinceWorldStart : "الوقت المنقضي",  

	edu.cmu.cs.stage3.alice.core.question.time.Year : "عام", 
	edu.cmu.cs.stage3.alice.core.question.time.MonthOfYear : "شهر من عام", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfYear : "يوم من عام", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfMonth : "يوم من شهر", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeek : "يوم من اسبوع", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeekInMonth : "يوم من الاسبوع من الشهر", 
	edu.cmu.cs.stage3.alice.core.question.time.IsAM : " هل صباحا", 
	edu.cmu.cs.stage3.alice.core.question.time.IsPM : " هل مساء ", 
	edu.cmu.cs.stage3.alice.core.question.time.HourOfAMOrPM : "ساعة صباحا أو مساء", 
	edu.cmu.cs.stage3.alice.core.question.time.HourOfDay : "ساعة من يوم", 
	edu.cmu.cs.stage3.alice.core.question.time.MinuteOfHour : "دقيقة من ساعة", 
	edu.cmu.cs.stage3.alice.core.question.time.SecondOfMinute : "ثانية من دقيقة", 

	edu.cmu.cs.stage3.alice.core.question.RandomBoolean : "اختر صحيح <probabilityOfTrue> من الوقت",
	edu.cmu.cs.stage3.alice.core.question.RandomNumber : "رقم عشوائي",

	edu.cmu.cs.stage3.alice.core.question.list.Contains : "<list> تحتوي <item>",
	edu.cmu.cs.stage3.alice.core.question.list.FirstIndexOfItem : "الرقم التسلسلي الأول من <item> من <list>",
	edu.cmu.cs.stage3.alice.core.question.list.IsEmpty : "هل <list> فارغة",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtBeginning : " <list>العنصر الأول من ",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtEnd : " <list>العنصر الأخير من ",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex : "العنصر <index> من <list>",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtRandomIndex : "عنصر عشوائي من <list>",
	edu.cmu.cs.stage3.alice.core.question.list.LastIndexOfItem : "الرقم التسلسلي الأخير <item> من <list>",
	edu.cmu.cs.stage3.alice.core.question.list.Size : "حجم <list>",

	edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex : "العنصر <index> من <<<array>>>",
	edu.cmu.cs.stage3.alice.core.question.array.Size : "حجم <<<array>>>",

	edu.cmu.cs.stage3.alice.core.question.IsAbove : "<<<subject>>> هل أعلى <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsBehind : "<<<subject>>> هل وراء <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsBelow : "<<<subject>>> هل أسفل <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsInFrontOf : "<<<subject>>> هل أمام <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsLeftOf : "<<<subject>>> هل على يسار <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsRightOf : "<<<subject>>> هل على يمين <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsSmallerThan : "<<<subject>>> هل أصغر من <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsLargerThan : "<<<subject>>> هل أكبر من<<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsNarrowerThan : "<<<subject>>> هل أضيق من <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsWiderThan : "<<<subject>>> هل أوسع من <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsShorterThan : "<<<subject>>> هل أقصر من <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsTallerThan : "<<<subject>>> هل أطول من <<object>>",
 
	edu.cmu.cs.stage3.alice.core.question.IsCloseTo : "<<<subject>>> هل ضمن <threshold> من <object>",
	edu.cmu.cs.stage3.alice.core.question.IsFarFrom : "<<<subject>>> هل على الأقل <threshold> يبتعد عن <object>",
	edu.cmu.cs.stage3.alice.core.question.DistanceTo : "<<<subject>>> المسافة إلى <<object>>",

	edu.cmu.cs.stage3.alice.core.question.DistanceToTheLeftOf : "<<<subject>>> المسافة إلى اليسار من <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceToTheRightOf : "<<<subject>>> المسافة إلى اليمين من <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceAbove : "<<<subject>>> المسافة أعلى <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceBelow : "<<<subject>>> المسافة اسفل <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceInFrontOf : "<<<subject>>> المسافة أمام <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceBehind : "<<<subject>>> المسافة خلف <<object>>",

	edu.cmu.cs.stage3.alice.core.question.vector3.X : "<<<المسافة لليمين",
	edu.cmu.cs.stage3.alice.core.question.vector3.Y : "<<<vector3>>>'s المسافة للأعلى",
	edu.cmu.cs.stage3.alice.core.question.vector3.Z : "<<<vector3>>>'s المسافة للأمام",

	edu.cmu.cs.stage3.alice.core.question.PickQuestion : "الكائن تحت مؤشر الفأرة",

	edu.cmu.cs.stage3.alice.core.question.RightUpForward : "<right>, <up>, <forward>",

	edu.cmu.cs.stage3.alice.core.question.Pose : "<<<subject>>>'s الوضع الحالي",
	edu.cmu.cs.stage3.alice.core.question.PropertyValue : "<<<element>>>.<propertyName>",

	edu.cmu.cs.stage3.alice.core.question.visualization.model.Item : "قيمة ال <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.model.SetItem : "اجعل <<<subject>>> = <item>",

	edu.cmu.cs.stage3.alice.core.question.visualization.array.ItemAtIndex : "القيمة في <<<subject>>>[ <index> ]",
	edu.cmu.cs.stage3.alice.core.responses.visualization.array.SetItemAtIndex : "اجعل <<<subject>>>[ <index> ] = <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.array.Size : "<<<subject>>>'s حجم",

	edu.cmu.cs.stage3.alice.core.question.visualization.list.Size : "<<<subject>>>'s حجم",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.Contains : "<<<subject>>> يحتوي <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.IsEmpty : "<<<subject>>> فارغ",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.FirstIndexOfItem : "<<<subject>>>'s الرقم التسلسلي الأول من <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.LastIndexOfItem : "<<<subject>>>'s الرقم التسلسلي الأخير من <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtBeginning : "<<<subject>>>'s العنصر في البداية",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtEnd : "<<<subject>>>'s العنصر في النهاية",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtIndex : "<<<subject>>>'s العنصر في الرقم التسلسلي <index>",
	
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.InsertItemAtBeginning : "أضف <item> في بداية <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.InsertItemAtEnd : "أضف <item> في نهاية <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.InsertItemAtIndex : "أضف <item> في <index> من <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.RemoveItemFromBeginning : "أزل العنصر من بداية <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.RemoveItemFromEnd : "أزل العنصر من نهاية <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.RemoveItemFromIndex : "أزل العنصر من <index> من <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.Clear : "احذف <<<subject>>>",
}



##################
# Name Config
##################

nameMap = {
	"edu.cmu.cs.stage3.alice.core.responses.DoInOrder" : "افعل بالتتابع",
	"edu.cmu.cs.stage3.alice.core.responses.DoTogether" : "افعل بالتزامن",
	"edu.cmu.cs.stage3.alice.core.responses.IfElseInOrder" : "إذا/غير ذلك",
	"edu.cmu.cs.stage3.alice.core.responses.LoopNInOrder" : "حلقة تكرار",
	"edu.cmu.cs.stage3.alice.core.responses.WhileLoopInOrder" : "بينما",
	"edu.cmu.cs.stage3.alice.core.responses.ForEachInOrder" : "للجميع بالتتابع",
	"edu.cmu.cs.stage3.alice.core.responses.ForEachTogether" : "للجميع بالتزامن",
	"edu.cmu.cs.stage3.alice.core.responses.Print" : "اطبع",
	"edu.cmu.cs.stage3.alice.core.responses.QuaternionAnimation.quaternion" : "بالمقابل",
	"edu.cmu.cs.stage3.alice.core.responses.PointOfViewAnimation.pointOfView" : "زاوية النظر ل",
	"edu.cmu.cs.stage3.alice.core.responses.PositionAnimation.position" : "بالمقابل",

	"edu.cmu.cs.stage3.alice.core.question.userdefined.Return" : "إرجاع",

	"edu.cmu.cs.stage3.alice.core.behaviors.WorldStartBehavior" : "عندما يبدأ العالم",
	"edu.cmu.cs.stage3.alice.core.behaviors.WorldIsRunningBehavior" : "أثناء تشغيل العالم",
	"edu.cmu.cs.stage3.alice.core.behaviors.KeyClickBehavior" : "عندما <keyCode> يطبع",
	"edu.cmu.cs.stage3.alice.core.behaviors.KeyIsPressedBehavior" : "عندما <keyCode> يُضغط",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonClickBehavior" : "عندما <mouse> يضغط على <onWhat>",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonIsPressedBehavior" : "بينما <mouse> يضغط على <onWhat>",
	"edu.cmu.cs.stage3.alice.core.behaviors.ConditionalBehavior" : "بينما <condition> صحيح",
	"edu.cmu.cs.stage3.alice.core.behaviors.ConditionalTriggerBehavior" : "عندما <condition> يصبح صحيح",
	"edu.cmu.cs.stage3.alice.core.behaviors.VariableChangeBehavior" : "عندما <variable> يتغير",
	"edu.cmu.cs.stage3.alice.core.behaviors.MessageReceivedBehavior" : "عند تلقي رسالة بواسطة  <toWhom> من <fromWho>", 
	"edu.cmu.cs.stage3.alice.core.behaviors.DefaultMouseInteractionBehavior" : " دع <mouse> يتحرك <objects>",
	"edu.cmu.cs.stage3.alice.core.behaviors.KeyboardNavigationBehavior" : " دع <arrowKeys> يتحرك <subject>",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseNavigationBehavior" : " دع <mouse> يحرك الكاميرا",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseLookingBehavior" : " دع <mouse> يوجّهه الكاميرا",
	"edu.cmu.cs.stage3.alice.core.behaviors.SoundMarkerPassedBehavior" : "عندما علامة الصوت  <marker> يشتغل",
	"edu.cmu.cs.stage3.alice.core.behaviors.SoundLevelBehavior" : "عندما يكون مستوى تسجيل الصوت >= <level>",

	"edu.cmu.cs.stage3.alice.core.Model.opacity" : "عتامة",
	"edu.cmu.cs.stage3.alice.core.Model.diffuseColorMap" : "نسيج البشرة",
	"نشر خريطة ملونة" : " نسيج البشرة",
	"edu.cmu.cs.stage3.alice.core.Transformable.localTransformation" : "زاوية النظر",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonClickBehavior.onWhat" : "على ماذا",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonIsPressedBehavior.onWhat" : "على ماذا",
	"edu.cmu.cs.stage3.alice.core.question.IsCloseTo.threshold" : "ضمن",
	"edu.cmu.cs.stage3.alice.core.question.IsFarFrom.threshold" : "على الأقل",
	"edu.cmu.cs.stage3.alice.core.question.IsCloseTo.object" : "من",
	"edu.cmu.cs.stage3.alice.core.question.IsFarFrom.object" : "بعيد عن",

	"edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Renderer" : "DirectX 7",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.openglrenderer.Renderer" : "OpenGL",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.java3drenderer.Renderer" : "Java3D",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.Renderer" : "JOGL",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer.Renderer" : "لاشيء",

	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_AND_END_GENTLY : "رويدًا",
	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY : "ابدأ رويدًا",
	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY : "انهي رويدًا",
	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_AND_END_ABRUPTLY : "بشكل مفاجئ",

	edu.cmu.cs.stage3.alice.core.Direction.LEFT : "يسار",
	edu.cmu.cs.stage3.alice.core.Direction.RIGHT : "يمين",
	edu.cmu.cs.stage3.alice.core.Direction.UP : "أعلى",
	edu.cmu.cs.stage3.alice.core.Direction.DOWN : "أسفل",
	edu.cmu.cs.stage3.alice.core.Direction.FORWARD : "أمام",
	edu.cmu.cs.stage3.alice.core.Direction.BACKWARD : "خلف",

	edu.cmu.cs.stage3.alice.core.SpatialRelation.LEFT_OF : "من السيار ",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF : "من اليمين",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.ABOVE : "أعلى",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BELOW : "أسفل",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF : "أمام",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND : "خلف",

	edu.cmu.cs.stage3.alice.core.Dimension.ALL : "كل",
	edu.cmu.cs.stage3.alice.core.Dimension.LEFT_TO_RIGHT : "من اليسار إلى اليمين",
	edu.cmu.cs.stage3.alice.core.Dimension.TOP_TO_BOTTOM : "من الأعلى إلى الأسفل",
	edu.cmu.cs.stage3.alice.core.Dimension.FRONT_TO_BACK : "من الأمام إلى الخلف",

	edu.cmu.cs.stage3.alice.core.FogStyle.NONE : "بدون ضباب",
	edu.cmu.cs.stage3.alice.core.FogStyle.LINEAR : "مسافة",
	edu.cmu.cs.stage3.alice.core.FogStyle.EXPONENTIAL : "كثافة",

	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.SOLID : "صلب",
 	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.WIREFRAME : "الإطار السلكي",
	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.POINTS : "نقاط",

	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.NONE : "لاشيء",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.FLAT : "مسطح",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.SMOOTH : "أملس",

	java.lang.Boolean : "منطقي",
	java.lang.Number : "رقم",
	edu.cmu.cs.stage3.alice.core.Model : "كائن",

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

	edu.cmu.cs.stage3.util.HowMuch.INSTANCE : "الكائن فقط",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS : "الكائن والأجزاء",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS : "الكائن وكافة فروعه",
}

htmlNameMap = {
	"edu.cmu.cs.stage3.alice.core.Transformable" : "[Obj]",
	"edu.cmu.cs.stage3.alice.core.Model" : "[Obj]",
	"java.lang.Number" : "[123]",
	"java.lang.Boolean" : "[T/F]",
	"java.lang.String" : "[ABC]",
	"edu.cmu.cs.stage3.alice.scenegraph.Color" : "[Color]",
	"edu.cmu.cs.stage3.alice.core.TextureMap" : "[Texture]",
	"edu.cmu.cs.stage3.alice.core.Sound" : "[Sound]",
	"edu.cmu.cs.stage3.alice.core.Pose" : "[Pose]",
	"edu.cmu.cs.stage3.math.Vector3" : "[Pos]",
	"edu.cmu.cs.stage3.math.Quaternion" : "[Ori]",
	"edu.cmu.cs.stage3.math.Matrix44" : "[POV]",
	"edu.cmu.cs.stage3.alice.core.ReferenceFrame" : "[Obj]",
	"edu.cmu.cs.stage3.alice.core.Light" : "[Light]",
	"edu.cmu.cs.stage3.alice.core.Direction" : "[Direction]",
	"edu.cmu.cs.stage3.alice.core.Collection" : "]]]",
}


####################
# Color Config
####################

colorMap = {
	"disabledHTMLText": java.awt.Color( 200, 200, 200 ),
	"disabledHTML": java.awt.Color( 230, 230, 230 ),
	"DoInOrder" : java.awt.Color( 255, 255, 210 ),
	"DoTogether" : java.awt.Color( 238, 221, 255 ),
	"IfElseInOrder" : java.awt.Color( 204, 238, 221 ),
	"LoopNInOrder" : java.awt.Color( 221, 249, 249 ),
	"WhileLoopInOrder" : java.awt.Color( 204, 255, 221 ),
	"ForEach" : java.awt.Color( 255, 230, 230 ),
	"ForEachInOrder" : java.awt.Color( 255, 230, 230 ),
	"ForAllTogether" : java.awt.Color( 248, 221, 255 ),
	"Wait" : java.awt.Color( 255, 230, 180 ),
	"ScriptResponse" : java.awt.Color( 255, 230, 180 ),
	"ScriptDefinedResponse" : java.awt.Color( 255, 230, 180 ),
	"Print" : java.awt.Color( 255, 230, 180 ),
	"Comment" : java.awt.Color( 255, 255, 255 ),
	"Return" : java.awt.Color( 212, 204, 249 ),
	"PropertyAssignment" : java.awt.Color( 255, 230, 180 ),
	"accessibleMathTile" : java.awt.Color( 255, 230, 180 ),
	"dndHighlight" : java.awt.Color( 255, 255, 0 ),
	"dndHighlight2" : java.awt.Color( 0, 200, 0 ),
	"dndHighlight3" : java.awt.Color( 230, 0, 0 ),
	"propertyViewControllerBackground" : java.awt.Color( 240, 240, 255 ),
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
	"prototypeParameter" : java.awt.Color( 255, 255, 200 ),
	"elementDnDPanel" : java.awt.Color( 255, 230, 180 ),
	"elementPrototypeDnDPanel" : java.awt.Color( 255, 255, 255 ),
	"formattedElementViewController" : java.awt.Color( 255, 255, 255 ),
	"response" : java.awt.Color( 255, 230, 180 ),
	"question" : java.awt.Color( 212, 204, 249 ),
	"userDefinedResponse" : java.awt.Color( 255, 230, 180 ),
	"userDefinedQuestion" : java.awt.Color( 212, 204, 249 ),
	"userDefinedQuestionComponent" : java.awt.Color( 255, 230, 180 ),
	"commentForeground" : java.awt.Color( 0, 164, 0 ),
	"variableDnDPanel" : java.awt.Color( 255, 255, 200 ),
	"userDefinedQuestionEditor" : java.awt.Color( 225, 255, 195 ),
	"userDefinedResponseEditor" : java.awt.Color( 255, 255, 210 ),
	"editorHeaderColor" : java.awt.Color( 255, 255, 255 ),
	"behavior" : java.awt.Color( 203, 231, 236 ),
	"behaviorBackground" : java.awt.Color( 255, 255, 255 ),
	"makeSceneEditorBigBackground" : java.awt.Color( 0, 150, 0 ),
	"makeSceneEditorSmallBackground" : java.awt.Color( 0, 150, 0 ),
	"stdErrTextColor" : java.awt.Color( 52, 174, 32 ),
        "mainFontColor" : java.awt.Color(0,0,0),
}


#########################
# Experimental Features
#########################

experimental = 0



####################################
# transfer resource data to Alice
####################################

resourceTransferFile = os.path.join( JAlice.getAliceHomeDirectoryString(), "resources/common/ResourceTransfer.py" )
execfile( resourceTransferFile)
