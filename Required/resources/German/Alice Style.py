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
	edu.cmu.cs.stage3.alice.core.response.MoveAnimation : "<<<subject>>> bewege <<direction>><<amount>>",
	edu.cmu.cs.stage3.alice.core.response.MoveTowardAnimation : "<<<subject>>> bewege <<amount>> in Richtung <<target>>",
	edu.cmu.cs.stage3.alice.core.response.MoveAwayFromAnimation : "<<<subject>>> bewege <<amount>> weg von <<target>>",
	edu.cmu.cs.stage3.alice.core.response.TurnAnimation : "<<<subject>>> drehe <<direction>><<amount>>",
	edu.cmu.cs.stage3.alice.core.response.RollAnimation : "<<<subject>>> rolle <<direction>><<amount>>",
	edu.cmu.cs.stage3.alice.core.response.MoveAtSpeed : "<<<subject>>> bewege mit Geschwindigkeit <<direction>><<speed>>",
	edu.cmu.cs.stage3.alice.core.response.TurnAtSpeed : "<<<subject>>> drehe mit Geschwindigkeit <<direction>><<speed>>",
	edu.cmu.cs.stage3.alice.core.response.RollAtSpeed : "<<<subject>>> rolle mit Geschwindigkeit <<direction>><<speed>>",
	edu.cmu.cs.stage3.alice.core.response.ResizeAnimation : "<<<subject>>> ändere die Groesse <<amount>>",
	edu.cmu.cs.stage3.alice.core.response.PointAtAnimation : "<<<subject>>> zeige auf <<target>>",
	edu.cmu.cs.stage3.alice.core.response.TurnToFaceAnimation : "<<<subject>>> wende zu <<target>>",
	edu.cmu.cs.stage3.alice.core.response.TurnAwayFromAnimation : "<<<subject>>> abwenden von <<target>>",
	edu.cmu.cs.stage3.alice.core.response.PointAtConstraint : "<<<subject>>> wird gezwungen auf <<target>> zu zeigen",
	edu.cmu.cs.stage3.alice.core.response.TurnToFaceConstraint : "<<<subject>>> wird gezwungen, das <<target>> anzusehen",
	edu.cmu.cs.stage3.alice.core.response.TurnAwayFromConstraint : "<<<subject>>> wird gezwungen, vom <<target>> wegzusehen",
	edu.cmu.cs.stage3.alice.core.response.GetAGoodLookAtAnimation : "<<<subject>>> schaue zu <<target>>",
	edu.cmu.cs.stage3.alice.core.response.StandUpAnimation : "<<<subject>>> aufstehen",
	edu.cmu.cs.stage3.alice.core.response.PositionAnimation : "<<<subject>>> bewege zum Objekt <<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.response.PlaceAnimation : "<<<subject>>> caitlin move to <<amount>><<spatialRelation>><<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.response.QuaternionAnimation : "<<<subject>>> ausrichten nach <<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation : "<<<subject>>> setze Blickwinkel auf <<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.response.PropertyAnimation : "<<<element>>> setze <propertyName> to <<value>>",
	edu.cmu.cs.stage3.alice.core.response.SoundResponse : "<<<subject>>> spiele Sound <<sound>>",
	edu.cmu.cs.stage3.alice.core.response.Wait : "Warte <<duration>>",
	edu.cmu.cs.stage3.alice.core.response.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.response.Print : "drucke <<text>> <<object>>",
	edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse : "<userDefinedResponse><requiredActualParameters>",
	edu.cmu.cs.stage3.alice.core.response.ScriptResponse : "Script <<script>>",
	edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse : "Script-Defined Response <<script>>",
	edu.cmu.cs.stage3.alice.core.response.SayAnimation : "<<<subject>>> sage <<what>>",
	edu.cmu.cs.stage3.alice.core.response.ThinkAnimation : "<<<subject>>> denke <<what>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse : "Position Schlüsselbildanimation <<subject>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse : "Orientierung Schlüsselbildanimation <<subject>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse : "skaliere Schlüsselbildanimation <<subject>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse : "Schlüsselbildanimation <<subject>>",
	edu.cmu.cs.stage3.alice.core.response.PoseAnimation : "<<<subject>>> setze Körperhaltung <<pose>>",
	edu.cmu.cs.stage3.alice.core.response.Increment : "erhoehe <<<variable>>> um 1",
	edu.cmu.cs.stage3.alice.core.response.Decrement : "verringere <<<variable>>> um 1",

	edu.cmu.cs.stage3.alice.core.response.VehiclePropertyAnimation : "<element> setze <propertyName> auf <value>",

	edu.cmu.cs.stage3.alice.core.response.list.InsertItemAtBeginning : "einfuegen <item> am Anfang von <<<list>>>",
	edu.cmu.cs.stage3.alice.core.response.list.InsertItemAtEnd : "einfuegen <item> am Ende von <<<list>>>",
	edu.cmu.cs.stage3.alice.core.response.list.InsertItemAtIndex : "einfuegen <item> an der Stelle <index> von <<<list>>>",
	edu.cmu.cs.stage3.alice.core.response.list.RemoveItemFromBeginning : "entferne Gegenstand am Anfang von <<<list>>>",
	edu.cmu.cs.stage3.alice.core.response.list.RemoveItemFromEnd : "entferne Gegenstand am Ende von <<<list>>>",
	edu.cmu.cs.stage3.alice.core.response.list.RemoveItemFromIndex : "entferne Gegenstand von der Position <index> von <<<list>>>",
	edu.cmu.cs.stage3.alice.core.response.list.Clear : "entferne alle Gegenstände von <<<list>>>",

	edu.cmu.cs.stage3.alice.core.response.array.SetItemAtIndex : "setze Gegenstand <index> zu <item> in <<<array>>>",

	edu.cmu.cs.stage3.alice.core.response.vector3.SetX : "setze <<<vector3>>>Abstand rechts <<value>>",
	edu.cmu.cs.stage3.alice.core.response.vector3.SetY : "setze <<<vector3>>>Abstand oben <<value>>",
	edu.cmu.cs.stage3.alice.core.response.vector3.SetZ : "setze <<<vector3>>>Abstand nach vorn <<value>>",

	edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion : "<userDefinedQuestion><requiredActualParameters>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Return : "zurück <<value>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Print : "drucke <<text>> <<object>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment : "<element> setze <propertyName> zu <value>",

	edu.cmu.cs.stage3.alice.core.question.PartKeyed : "<<<owner>>>'s part named <key>",

	edu.cmu.cs.stage3.alice.core.question.Width : "<<<subject>>>Breite",
	edu.cmu.cs.stage3.alice.core.question.Height : "<<<subject>>>Hoehe",
	edu.cmu.cs.stage3.alice.core.question.Depth : "<<<subject>>>Tiefe",
	edu.cmu.cs.stage3.alice.core.question.Quaternion : "<<<subject>>>quaternion",
	edu.cmu.cs.stage3.alice.core.question.Position : "<<<subject>>>Position",
	edu.cmu.cs.stage3.alice.core.question.PointOfView : "<<<subject>>>Blickwinkel",

	edu.cmu.cs.stage3.alice.core.question.Not : "nicht <a>",
	edu.cmu.cs.stage3.alice.core.question.And : "beide <a> und <b>",
	edu.cmu.cs.stage3.alice.core.question.Or : "entweder <a> oder <b>, oder beide",

	edu.cmu.cs.stage3.alice.core.question.StringConcatQuestion : "<a> verbinden mit <b>",
	edu.cmu.cs.stage3.alice.core.question.ToStringQuestion : "<what> als Zeichenkette",

	edu.cmu.cs.stage3.alice.core.question.StringToUpperCaseQuestion : "<a> to uppercase",
	edu.cmu.cs.stage3.alice.core.question.StringToLowerCaseQuestion : "<a> to lowercase",

	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber : "frage User nach einer Zahl <<question>>",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo : "frage User nach ja oder nein <<question>>",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString : "frage User nach einer Zeichenkette <<question>>",

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

	edu.cmu.cs.stage3.alice.core.question.math.Min : "Minimum von <a> und <b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Max : "Maximum von <a> und <b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Abs : "absoluter Wert von <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Sqrt : "Quadratwurzel aus <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Floor : "Untergrenze <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Ceil : "Obergrenze <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Sin : "sin <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Cos : "cos <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Tan : "tan <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ASin : "arcsin <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ACos : "arccos <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ATan : "arctan <a>",
	edu.cmu.cs.stage3.alice.core.question.math.ATan2 : "arctan2 <a><b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Pow : "<a> erhöht zur <b> Potenz", 
	edu.cmu.cs.stage3.alice.core.question.math.Log : "natürlicher Logarithmus von <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Exp : "e erhöht zur <a> Potenz", 
	edu.cmu.cs.stage3.alice.core.question.math.IEEERemainder : "IEEERemainder von <a>/<b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Int : "int <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Round : "runde <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ToDegrees : "<a> konvertiere von Radianten zu Grade", 
	edu.cmu.cs.stage3.alice.core.question.math.ToRadians : "<a> konvertiere von Grade zu Radianten", 
	edu.cmu.cs.stage3.alice.core.question.math.SuperSqrt : "die <b>. Wurzel aus <a>",

	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromLeftEdge : "Abstand der Maus vom linken Rand", 
	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromTopEdge : "Abstand der Maus vom oberen Rand", 

	edu.cmu.cs.stage3.alice.core.question.time.TimeElapsedSinceWorldStart : "abgelaufene Zeit", 

	edu.cmu.cs.stage3.alice.core.question.time.Year : "Jahr", 
	edu.cmu.cs.stage3.alice.core.question.time.MonthOfYear : "Monat des Jahres", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfYear : "Tag des Jahres", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfMonth : "Tag des Monats", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeek : "Wochentag", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeekInMonth : "Wochentag im Monat", 
	edu.cmu.cs.stage3.alice.core.question.time.IsAM : "ist Vormittag", 
	edu.cmu.cs.stage3.alice.core.question.time.IsPM : "ist Nachmittag", 
	edu.cmu.cs.stage3.alice.core.question.time.HourOfAMOrPM : "Stunde vom Vormittag oder Nachmittag", 
	edu.cmu.cs.stage3.alice.core.question.time.HourOfDay : "Stundes des Tages", 
	edu.cmu.cs.stage3.alice.core.question.time.MinuteOfHour : "Minute einer Stunde", 
	edu.cmu.cs.stage3.alice.core.question.time.SecondOfMinute : "Sekunde einer Minute", 

	edu.cmu.cs.stage3.alice.core.question.RandomBoolean : "waehle richtige <probabilityOfTrue> der Zeit",
	edu.cmu.cs.stage3.alice.core.question.RandomNumber : "Zufallszahl",

	edu.cmu.cs.stage3.alice.core.question.list.Contains : "<list> enthaelt <item>",
	edu.cmu.cs.stage3.alice.core.question.list.FirstIndexOfItem : "erster Index von <item> von <list>",
	edu.cmu.cs.stage3.alice.core.question.list.IsEmpty : "ist <list> leer",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtBeginning : "erster Item von <list>",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtEnd : "letzter Item von <list>",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex : "Item <index> von <list>",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtRandomIndex : "Zufallitem von <list>",
	edu.cmu.cs.stage3.alice.core.question.list.LastIndexOfItem : "letzter Index von <item> von <list>",
	edu.cmu.cs.stage3.alice.core.question.list.Size : "Groesse von <list>",

	edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex : "Item <index> von <<<array>>>",
	edu.cmu.cs.stage3.alice.core.question.array.Size : "Groesse von <<<array>>>",

	edu.cmu.cs.stage3.alice.core.question.IsAbove : "<<<subject>>> ist oberhalb <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsBehind : "<<<subject>>> ist hinter <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsBelow : "<<<subject>>> ist unter <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsInFrontOf : "<<<subject>>> ist vor <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsLeftOf : "<<<subject>>> ist links von <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsRightOf : "<<<subject>>> ist rechts von <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsSmallerThan : "<<<subject>>> ist kleiner als <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsLargerThan : "<<<subject>>> ist groesser als <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsNarrowerThan : "<<<subject>>> ist naeher als <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsWiderThan : "<<<subject>>> ist weiter als <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsShorterThan : "<<<subject>>> ist kuerzer als <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsTallerThan : "<<<subject>>> ist laenger als <<object>>",
 
	edu.cmu.cs.stage3.alice.core.question.IsCloseTo : "<<<subject>>> ist innerhalb <threshold> von <object>",
	edu.cmu.cs.stage3.alice.core.question.IsFarFrom : "<<<subject>>> ist mindestens <threshold> weg von <object>",
	edu.cmu.cs.stage3.alice.core.question.DistanceTo : "<<<subject>>> Entfernung zu <<object>>",

	edu.cmu.cs.stage3.alice.core.question.DistanceToTheLeftOf : "<<<subject>>> Entfernung zur linken Seite von <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceToTheRightOf : "<<<subject>>> Entfernung zur rechten Seite von <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceAbove : "<<<subject>>> Entfernung über <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceBelow : "<<<subject>>> Entfernung unterhalb <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceInFrontOf : "<<<subject>>> Entfernung vor <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceBehind : "<<<subject>>> Entfernung hinter <<object>>",

	edu.cmu.cs.stage3.alice.core.question.vector3.X : "<<<vector3>>>Entfernung nach rechts",
	edu.cmu.cs.stage3.alice.core.question.vector3.Y : "<<<vector3>>>Entfernung nach oben",
	edu.cmu.cs.stage3.alice.core.question.vector3.Z : "<<<vector3>>>Entfernung nach vorn",

	edu.cmu.cs.stage3.alice.core.question.PickQuestion : "Objekt unter dem Mauszeiger",

	edu.cmu.cs.stage3.alice.core.question.RightUpForward : "<right>, <up>, <forward>",

	edu.cmu.cs.stage3.alice.core.question.Pose : "<<<subject>>>aktuelle Haltung",
	edu.cmu.cs.stage3.alice.core.question.PropertyValue : "<<<element>>>.<propertyName>",

	edu.cmu.cs.stage3.alice.core.question.visualization.model.Item : "der Wert von <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.response.visualization.model.SetItem : "lass <<<subject>>> = <item>",

	edu.cmu.cs.stage3.alice.core.question.visualization.array.ItemAtIndex : "der Wert zu <<<subject>>>[ <index> ]",
	edu.cmu.cs.stage3.alice.core.response.visualization.array.SetItemAtIndex : "lass <<<subject>>>[ <index> ] = <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.array.Size : "<<<subject>>>Groesse",

	edu.cmu.cs.stage3.alice.core.question.visualization.list.Size : "<<<subject>>>Groesse",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.Contains : "<<<subject>>> enthaelt <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.IsEmpty : "<<<subject>>> ist leer",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.FirstIndexOfItem : "<<<subject>>>erster Index von <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.LastIndexOfItem : "<<<subject>>>letzter Index von <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtBeginning : "<<<subject>>>Item am Anfang",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtEnd : "<<<subject>>>Item am Ende",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtIndex : "<<<subject>>>Item in Index <index>",
	
	edu.cmu.cs.stage3.alice.core.response.visualization.list.InsertItemAtBeginning : "einfuegen <item> am Anfang von <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.response.visualization.list.InsertItemAtEnd : "einfuegen <item> am Ende von <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.response.visualization.list.InsertItemAtIndex : "einfuegen <item> in <index> von <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.response.visualization.list.RemoveItemFromBeginning : "entferne Item am Anfang von <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.response.visualization.list.RemoveItemFromEnd : "entferne Item am Ende von <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.response.visualization.list.RemoveItemFromIndex : "entferne Item von <index> von <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.response.visualization.list.Clear : "loesche <<<subject>>>",
}



##################
# Name Config
##################

nameMap = {
	"edu.cmu.cs.stage3.alice.core.response.DoInOrder" : "in Reihenfolge ausfuehren",
	"edu.cmu.cs.stage3.alice.core.response.DoTogether" : "zusammen ausfuehren",
	"edu.cmu.cs.stage3.alice.core.response.IfElseInOrder" : "wenn/dann",
	"edu.cmu.cs.stage3.alice.core.response.LoopNInOrder" : "Schleife",
	"edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder" : "waehrend",
	"edu.cmu.cs.stage3.alice.core.response.ForEachInOrder" : "fuer alle in Reihenfolge",
	"edu.cmu.cs.stage3.alice.core.response.ForEachTogether" : "fuer alle zusammen",
	"edu.cmu.cs.stage3.alice.core.response.Print" : "drucken",
	"edu.cmu.cs.stage3.alice.core.response.QuaternionAnimation.quaternion" : "kompensiert durch",
	"edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation.pointOfView" : "Blickwinkel von",
	"edu.cmu.cs.stage3.alice.core.response.PositionAnimation.position" : "kompensiert durch",

	"edu.cmu.cs.stage3.alice.core.question.userdefined.Return" : "zurueck",

	"edu.cmu.cs.stage3.alice.core.behavior.WorldStartBehavior" : "Wenn die Welt beginnt",
	"edu.cmu.cs.stage3.alice.core.behavior.WorldIsRunningBehavior" : "Waehrend die Welt laeuft",
	"edu.cmu.cs.stage3.alice.core.behavior.KeyClickBehavior" : "Wenn <keyCode> gedrückt wird",
	"edu.cmu.cs.stage3.alice.core.behavior.KeyIsPressedBehavior" : "Waehrend <keyCode> gedrueckt ist",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseButtonClickBehavior" : "Wenn <mouse> auf <onWhat> klickt",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseButtonIsPressedBehavior" : "Waehrend <mouse> auf <onWhat> gedrueckt ist",
	"edu.cmu.cs.stage3.alice.core.behavior.ConditionalBehavior" : "Waehrend <condition> wahr ist",
	"edu.cmu.cs.stage3.alice.core.behavior.ConditionalTriggerBehavior" : "Wenn <condition> wahr wird",
	"edu.cmu.cs.stage3.alice.core.behavior.VariableChangeBehavior" : "Wenn <variable> sich aendert",
	"edu.cmu.cs.stage3.alice.core.behavior.MessageReceivedBehavior" : "Wenn eine Nachricht an <toWhom> von <fromWho> empfangen wurde", 
	"edu.cmu.cs.stage3.alice.core.behavior.DefaultMouseInteractionBehavior" : "Lass <mouse> verschieben <objects>",
	"edu.cmu.cs.stage3.alice.core.behavior.KeyboardNavigationBehavior" : "Lass <arrowKeys> verschieben <subject>",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseNavigationBehavior" : "Lass <mouse> die Kamera verschieben",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseLookingBehavior" : "Lass <mouse> die Kamera ausrichten",
	"edu.cmu.cs.stage3.alice.core.behavior.SoundMarkerPassedBehavior" : "Wenn der Soundmarker <marker> spielt",
	"edu.cmu.cs.stage3.alice.core.behavior.SoundLevelBehavior" : "Wenn das Sound-Aufnahmelevel  >= <level> ist",

	"edu.cmu.cs.stage3.alice.core.Model.opacity" : "Deckkraft",
	"edu.cmu.cs.stage3.alice.core.Model.diffuseColorMap" : "Hautbeschaffenheit",
	"diffuseColorMap" : "Hautbeschaffenheit",
	"edu.cmu.cs.stage3.alice.core.Transformable.localTransformation" : "Blickwinkel",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseButtonClickBehavior.onWhat" : "auf was",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseButtonIsPressedBehavior.onWhat" : "auf was",
	"edu.cmu.cs.stage3.alice.core.question.IsCloseTo.threshold" : "is innen",
	"edu.cmu.cs.stage3.alice.core.question.IsFarFrom.threshold" : "ist mindestens",
	"edu.cmu.cs.stage3.alice.core.question.IsCloseTo.object" : "aus",
	"edu.cmu.cs.stage3.alice.core.question.IsFarFrom.object" : "weg von",

	"edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Renderer" : "DirectX 7",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.openglrenderer.Renderer" : "OpenGL",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.java3drenderer.Renderer" : "Java3D",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.Renderer" : "JOGL",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer.Renderer" : "None",

	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_AND_END_GENTLY : "langsam",
	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY : "beginne langsam",
	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY : "ende langsam",
	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_AND_END_ABRUPTLY : "abrupt",

	edu.cmu.cs.stage3.alice.core.Direction.LEFT : "links",
	edu.cmu.cs.stage3.alice.core.Direction.RIGHT : "rechts",
	edu.cmu.cs.stage3.alice.core.Direction.UP : "hoch",
	edu.cmu.cs.stage3.alice.core.Direction.DOWN : "runter",
	edu.cmu.cs.stage3.alice.core.Direction.FORWARD : "vorwaerts",
	edu.cmu.cs.stage3.alice.core.Direction.BACKWARD : "rueckwaerts",

	edu.cmu.cs.stage3.alice.core.SpatialRelation.LEFT_OF : "links von",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF : "rechts von",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.ABOVE : "ueber",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BELOW : "unter",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF : "vor",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND : "hinter",

	edu.cmu.cs.stage3.alice.core.Dimension.ALL : "alle",
	edu.cmu.cs.stage3.alice.core.Dimension.LEFT_TO_RIGHT : "links nach rechts",
	edu.cmu.cs.stage3.alice.core.Dimension.TOP_TO_BOTTOM : "oben nach unten",
	edu.cmu.cs.stage3.alice.core.Dimension.FRONT_TO_BACK : "vorn nach hinten",

	edu.cmu.cs.stage3.alice.core.FogStyle.NONE : "kein Nebel",
	edu.cmu.cs.stage3.alice.core.FogStyle.LINEAR : "Abstand",
	edu.cmu.cs.stage3.alice.core.FogStyle.EXPONENTIAL : "Dichte",

	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.SOLID : "Koerper",
 	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.WIREFRAME : "Drahtmodell",
	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.POINTS : "Punkte",

	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.NONE : "nichts",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.FLAT : "flach",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.SMOOTH : "glatt",

	java.lang.Boolean : "Boolean",
	java.lang.Number : "Zahl",
	edu.cmu.cs.stage3.alice.core.Model : "Objekt",

	Boolean.TRUE : "wahr",
	Boolean.FALSE : "falsch",

	edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE : "weiss",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK : "schwarz",
	edu.cmu.cs.stage3.alice.scenegraph.Color.RED : "rot",
	edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN : "gruen",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE : "blau",
	edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW : "gelb",
	edu.cmu.cs.stage3.alice.scenegraph.Color.PURPLE : "lila",
	edu.cmu.cs.stage3.alice.scenegraph.Color.ORANGE : "orange",
	edu.cmu.cs.stage3.alice.scenegraph.Color.PINK : "pink",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BROWN : "braun",
	edu.cmu.cs.stage3.alice.scenegraph.Color.CYAN : "cyan",
	edu.cmu.cs.stage3.alice.scenegraph.Color.MAGENTA : "magenta",
	edu.cmu.cs.stage3.alice.scenegraph.Color.GRAY : "grau",
	edu.cmu.cs.stage3.alice.scenegraph.Color.LIGHT_GRAY : "hellgrau",
	edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY : "dunkelgrau",

	edu.cmu.cs.stage3.util.HowMuch.INSTANCE : "nur Objekte",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS : "Objekte and Einzelteile",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS : "Objekte und alle Abkoemmlinge",
}

htmlNameMap = {
	"edu.cmu.cs.stage3.alice.core.Transformable" : "[Obj]",
	"edu.cmu.cs.stage3.alice.core.Model" : "[Obj]",
	"java.lang.Number" : "[123]",
	"java.lang.Boolean" : "[T/F]",
	"java.lang.String" : "[ABC]",
	"edu.cmu.cs.stage3.alice.scenegraph.Color" : "[Farbe]",
	"edu.cmu.cs.stage3.alice.core.TextureMap" : "[Beschaffenheit]",
	"edu.cmu.cs.stage3.alice.core.Sound" : "[Sound]",
	"edu.cmu.cs.stage3.alice.core.Pose" : "[Haltung]",
	"edu.cmu.cs.stage3.math.Vector3" : "[Pos]",
	"edu.cmu.cs.stage3.math.Quaternion" : "[Ori]",
	"edu.cmu.cs.stage3.math.Matrix44" : "[POV]",
	"edu.cmu.cs.stage3.alice.core.ReferenceFrame" : "[Obj]",
	"edu.cmu.cs.stage3.alice.core.Light" : "[Helligkeit]",
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

resourceTransferFile = os.path.join( JAlice.getAliceHomeDirectory(), "resources/common/ResourceTransfer.py" )
execfile( resourceTransferFile.getAbsolutePath() )
