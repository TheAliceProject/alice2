from edu.cmu.cs.stage3.alice.authoringtool import JAlice
from edu.cmu.cs.stage3.alice.core.question.array import ItemAtIndex
from edu.cmu.cs.stage3.alice.core.question.ask import AskUserForNumber
from edu.cmu.cs.stage3.alice.core.question.list import Contains
from edu.cmu.cs.stage3.alice.core.question.math import Min
from edu.cmu.cs.stage3.alice.core.question.mouse import DistanceFromLeftEdge
from edu.cmu.cs.stage3.alice.core.question.time import TimeElapsedSinceWorldStart
from edu.cmu.cs.stage3.alice.core.question.userdefined import CallToUserDefinedQuestion
from edu.cmu.cs.stage3.alice.core.question.vector3 import X
from edu.cmu.cs.stage3.alice.core.question.visualization.array import ItemAtIndex
from edu.cmu.cs.stage3.alice.core.question.visualization.list import Size
from edu.cmu.cs.stage3.alice.core.question.visualization.model import Item
from edu.cmu.cs.stage3.alice.core.responses.array import SetItemAtIndex
from edu.cmu.cs.stage3.alice.core.responses.list import InsertItemAtBeginning
from edu.cmu.cs.stage3.alice.core.responses.vector3 import SetX
from edu.cmu.cs.stage3.alice.core.responses.visualization.array import SetItemAtIndex
from edu.cmu.cs.stage3.alice.core.responses.visualization.list import InsertItemAtBeginning
from edu.cmu.cs.stage3.alice.core.responses.visualization.model import SetItem
from edu.cmu.cs.stage3.alice.core.styles import TraditionalAnimationStyle
from edu.cmu.cs.stage3.pratt.maxkeyframing import PositionKeyframeResponse
from edu.cmu.cs.stage3.util import StringTypePair
from java.lang import Boolean
import edu
import java
import os
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
	edu.cmu.cs.stage3.alice.core.responses.MoveAnimation : "<<<subject>>> mover <<direction>><<amount>>",
	edu.cmu.cs.stage3.alice.core.responses.MoveTowardAnimation : "<<<subject>>> acercarse <<amount>> a <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.MoveAwayFromAnimation : "<<<subject>>> alejarse <<amount>> de <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnAnimation : "<<<subject>>> girar <<direction>><<amount>>",
	edu.cmu.cs.stage3.alice.core.responses.RollAnimation : "<<<subject>>> rodar <<direction>><<amount>>",
	edu.cmu.cs.stage3.alice.core.responses.MoveAtSpeed : "<<<subject>>> mover con velocidad <<direction>><<speed>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnAtSpeed : "<<<subject>>> girar a la velocidad <<direction>><<speed>>",
	edu.cmu.cs.stage3.alice.core.responses.RollAtSpeed : "<<<subject>>> rodar a la velocidad <<direction>><<speed>>",
	edu.cmu.cs.stage3.alice.core.responses.ResizeAnimation : "<<<subject>>> cambiar el tamaño <<amount>>",
	edu.cmu.cs.stage3.alice.core.responses.PointAtAnimation : "<<<subject>>> girar apuntar a <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnToFaceAnimation : "<<<subject>>> girar para encarar a  <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnAwayFromAnimation : "<<<subject>>> girar para alejarse de <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.PointAtConstraint : "<<<subject>>> restringido para apuntar a <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnToFaceConstraint : "<<<subject>>> restringido a encarar <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnAwayFromConstraint : "<<<subject>>> restringido para alejarse de <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.GetAGoodLookAtAnimation : "<<<subject>>> tomar una buena mirada <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.StandUpAnimation : "<<<subject>>> pararse",
	edu.cmu.cs.stage3.alice.core.responses.PositionAnimation : "<<<subject>>> mover a<<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.responses.PlaceAnimation : "<<<subject>>> colocar <<amount>><<spatialRelation>><<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.responses.QuaternionAnimation : "<<<subject>>> orientar a <<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.responses.PointOfViewAnimation : "<<<subject>>> establece el punto de vista <<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.responses.PropertyAnimation : "<<<element>>> establecer <propertyName> a <<value>>",
	edu.cmu.cs.stage3.alice.core.responses.SoundResponse : "<<<subject>>> reproducir sonido <<sound>>",
	edu.cmu.cs.stage3.alice.core.responses.Wait : "esperar <<duration>>",
	edu.cmu.cs.stage3.alice.core.responses.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.responses.Print : "imprimir <<text>> <<object>>",
	edu.cmu.cs.stage3.alice.core.responses.CallToUserDefinedResponse : "<userDefinedResponse><requiredActualParameters>",
	edu.cmu.cs.stage3.alice.core.responses.ScriptResponse : "Guión (Script) <<script>>",
	edu.cmu.cs.stage3.alice.core.responses.ScriptDefinedResponse : "Respuesta definida por el Guión <<script>>",
	edu.cmu.cs.stage3.alice.core.responses.SayAnimation : "<<<subject>>> dice <<what>>",
	edu.cmu.cs.stage3.alice.core.responses.ThinkAnimation : "<<<subject>>> piensa <<what>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse : "position keyframe animation <<subject>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse : "orientation keyframe animation <<subject>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse : "scale keyframe animation <<subject>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse : "keyframe animation <<subject>>",
	edu.cmu.cs.stage3.alice.core.responses.PoseAnimation : "<<<subject>>> establecer pose <<pose>>",
	edu.cmu.cs.stage3.alice.core.responses.Increment : "aumentar  <<<variable>>> de 1 en 1",
	edu.cmu.cs.stage3.alice.core.responses.Decrement : "disminuir <<<variable>>> de 1 en 1",

	edu.cmu.cs.stage3.alice.core.responses.VehiclePropertyAnimation : "<element> establecer <propertyName> con valor <value>",

	edu.cmu.cs.stage3.alice.core.responses.list.InsertItemAtBeginning : "insertar <item> al principio de <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.InsertItemAtEnd : "insertar <item> al final de <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.InsertItemAtIndex : "insertar <item> en la posición <index> de <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.RemoveItemFromBeginning : "eliminar elemento del principio <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.RemoveItemFromEnd : "eliminar elemento del final<<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.RemoveItemFromIndex : "eliminar elemento con índice <index> en <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.Clear : "eliminar todos elementos en <<<list>>>",

	edu.cmu.cs.stage3.alice.core.responses.array.SetItemAtIndex : "establecer elemento en posición <index> con <item> en <<<array>>>",

	edu.cmu.cs.stage3.alice.core.responses.vector3.SetX : "valor <<value>> para la distancia a la derecha del <<<vector3>>>",
	edu.cmu.cs.stage3.alice.core.responses.vector3.SetY : "valor <<value>> para la distancia arriba del <<<vector3>>>",
	edu.cmu.cs.stage3.alice.core.responses.vector3.SetZ : "valor <<value>> para la distancia adelante del <<<vector3>>>",

	edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion : "<userDefinedQuestion><requiredActualParameters>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Return : "regresar <<value>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Print : "imprimir <<text>> <<object>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment : "el elemento <element> usa el valor <value> para establecer la propiedad <propertyName>",

	edu.cmu.cs.stage3.alice.core.question.PartKeyed : "parte <<<owner>>> se llamada <key>",

	edu.cmu.cs.stage3.alice.core.question.Width : "el ancho del <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.question.Height : "la altura del <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.question.Depth : "la profundidad del <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.question.Quaternion : "cauternio <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.question.Position : "la posición del <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.question.PointOfView : "punto de vista <<<subject>>>",

	edu.cmu.cs.stage3.alice.core.question.Not : "no <a>",
	edu.cmu.cs.stage3.alice.core.question.And : "ambos <a> y <b>",
	edu.cmu.cs.stage3.alice.core.question.Or : "ya sea <a> o <b>, o ambos",

	edu.cmu.cs.stage3.alice.core.question.StringConcatQuestion : "<a> unido con <b>",
	edu.cmu.cs.stage3.alice.core.question.ToStringQuestion : "<what> como secuencia de caracteres (string)",

	edu.cmu.cs.stage3.alice.core.question.StringToUpperCaseQuestion : "<a> to uppercase",
	edu.cmu.cs.stage3.alice.core.question.StringToLowerCaseQuestion : "<a> to lowercase",

	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber : "preguntar al usuario un número <<question>>",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo : "preguntar al usuario sí o no <<question>>",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString : "preguntar al usuario una secuencia de caracteres (string) <<question>>",

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

	edu.cmu.cs.stage3.alice.core.question.math.Min : "valor mínimo de <a> y <b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Max : "valor máximo de <a>  y <b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Abs : "valor absoluto de <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Sqrt : "la raíz cuadrada de <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Floor : "valor mínimo de <a> como número entero", 
	edu.cmu.cs.stage3.alice.core.question.math.Ceil : "valor máximo de <a> como número entero", 
	edu.cmu.cs.stage3.alice.core.question.math.Sin : "seno <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Cos : "coseno <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Tan : "tangente <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ASin : "arco seno<a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ACos : "arco coseno <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ATan : "arco tangente <a>",
	edu.cmu.cs.stage3.alice.core.question.math.ATan2 : "arco tangente <a><b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Pow : "<a> elevado a la potencia de <b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Log : "logaritmo natural de <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Exp : "número e elevado a la potencia de <a> power", 
	edu.cmu.cs.stage3.alice.core.question.math.IEEERemainder : "el resto de IEEE<a>/<b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Int : "int <a>",
	edu.cmu.cs.stage3.alice.core.question.math.Round : "redondear <a>",
	edu.cmu.cs.stage3.alice.core.question.math.ToDegrees : "<a> de radianes a grados", 
	edu.cmu.cs.stage3.alice.core.question.math.ToRadians : "<a> de grados a radianes", 
	edu.cmu.cs.stage3.alice.core.question.math.SuperSqrt : "la raíz <b> de <a>",

	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromLeftEdge : "distancia del ratón al borde izquierdo", 
	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromTopEdge : "distancia del ratón al borde superior", 

	edu.cmu.cs.stage3.alice.core.question.time.TimeElapsedSinceWorldStart : "tiempo transcurrido", 

	edu.cmu.cs.stage3.alice.core.question.time.Year : "año", 
	edu.cmu.cs.stage3.alice.core.question.time.MonthOfYear : "mes del año", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfYear : "día del año", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfMonth : "día del mes", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeek : "día de la semana", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeekInMonth : "día de la semana en el mes", 
	edu.cmu.cs.stage3.alice.core.question.time.IsAM : "es AM", 
	edu.cmu.cs.stage3.alice.core.question.time.IsPM : "es PM", 
	edu.cmu.cs.stage3.alice.core.question.time.HourOfAMOrPM : "hora de AM o PM", 
	edu.cmu.cs.stage3.alice.core.question.time.HourOfDay : "hora del día", 
	edu.cmu.cs.stage3.alice.core.question.time.MinuteOfHour : "minuto de la hora", 
	edu.cmu.cs.stage3.alice.core.question.time.SecondOfMinute : "segundo del minuto", 

	edu.cmu.cs.stage3.alice.core.question.RandomBoolean : "escoger verdadero <probabilityOfTrue> del tiempo",
	edu.cmu.cs.stage3.alice.core.question.RandomNumber : "número aleatorio",

	edu.cmu.cs.stage3.alice.core.question.list.Contains : "<list> contiene <item>",
	edu.cmu.cs.stage3.alice.core.question.list.FirstIndexOfItem : "el primer índice de <item> en <list>",
	edu.cmu.cs.stage3.alice.core.question.list.IsEmpty : " <list> no tiene ningún valor",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtBeginning : "el primer elemento en <list>",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtEnd : "el último elemento en <list>",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex : "elemento <index> en <list>",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtRandomIndex : "elemento al azar en <list>",
	edu.cmu.cs.stage3.alice.core.question.list.LastIndexOfItem : "último índice de <item> en<list>",
	edu.cmu.cs.stage3.alice.core.question.list.Size : "tamaño de <list>",

	edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex : "elemento <index> en <<<array>>>",
	edu.cmu.cs.stage3.alice.core.question.array.Size : "tamaño de <<<array>>>",

	edu.cmu.cs.stage3.alice.core.question.IsAbove : "<<<subject>>> está por encima de <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsBehind : "<<<subject>>> está detrás de <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsBelow : "<<<subject>>> está por denajo de <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsInFrontOf : "<<<subject>>> está en frente de <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsLeftOf : "<<<subject>>> está a la izquierda de <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsRightOf : "<<<subject>>> está a la derecha de <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsSmallerThan : "<<<subject>>> es más pequeño que <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsLargerThan : "<<<subject>>> es más grande que <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsNarrowerThan : "<<<subject>>> es más angosto que <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsWiderThan : "<<<subject>>> es más ancho que <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsShorterThan : "<<<subject>>> es más bajo que <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsTallerThan : "<<<subject>>> es más alto que <<object>>",
 
	edu.cmu.cs.stage3.alice.core.question.IsCloseTo : "<<<subject>>> está dentro del rango <threshold> del <object>",
	edu.cmu.cs.stage3.alice.core.question.IsFarFrom : "<<<subject>>> está como mínimo <threshold> alejado del <object>",
	edu.cmu.cs.stage3.alice.core.question.DistanceTo : "<<<subject>>> distancia a<<object>>",

	edu.cmu.cs.stage3.alice.core.question.DistanceToTheLeftOf : "<<<subject>>> distancia a la izquierda de <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceToTheRightOf : "<<<subject>>> distancia a la derecha de  <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceAbove : "<<<subject>>> distancia desde arriba <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceBelow : "<<<subject>>> distancia desde abajo <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceInFrontOf : "<<<subject>>> distance en frente de  <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceBehind : "<<<subject>>> distancia detrás de <<object>>",

	edu.cmu.cs.stage3.alice.core.question.vector3.X : "distancia a la derecha de <<<vector3>>>",
	edu.cmu.cs.stage3.alice.core.question.vector3.Y : "distancia de <<<vector3>>> hasta",
	edu.cmu.cs.stage3.alice.core.question.vector3.Z : "distancia en frente de <<<vector3>>>",

	edu.cmu.cs.stage3.alice.core.question.PickQuestion : "objeto debajo del marcador del ratón",

	edu.cmu.cs.stage3.alice.core.question.RightUpForward : "<right>, <up>, <forward>",

	edu.cmu.cs.stage3.alice.core.question.Pose : "la pose actual del <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.question.PropertyValue : "<<<element>>>.<propertyName>",

	edu.cmu.cs.stage3.alice.core.question.visualization.model.Item : "el valor de <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.model.SetItem : "permitir <<<subject>>> = <item>",

	edu.cmu.cs.stage3.alice.core.question.visualization.array.ItemAtIndex : "el valor en <<<subject>>>[ <index> ]",
	edu.cmu.cs.stage3.alice.core.responses.visualization.array.SetItemAtIndex : "permitir <<<subject>>>[ <index> ] = <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.array.Size : "<<<subject>>> tiene tamaño",

	edu.cmu.cs.stage3.alice.core.question.visualization.list.Size : "<<<subject>>> tiene tamaño",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.Contains : "<<<subject>>> contiene <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.IsEmpty : "<<<subject>>> no tiene valor",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.FirstIndexOfItem : "el primer índice de <<<subject>>> es <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.LastIndexOfItem : "el último índice de <<<subject>>> es <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtBeginning : "el elemento de <<<subject>>> está al principio",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtEnd : "el elemento de <<<subject>>> está al final",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtIndex : "el elemento <<<subject>>> en el índice <index>",
	
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.InsertItemAtBeginning : "agregar <item> al principio de <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.InsertItemAtEnd : "agregar <item> al final de <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.InsertItemAtIndex : "añadir el <item> en el ínidce <index> del <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.RemoveItemFromBeginning : "eliminar este elemento del principio del <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.RemoveItemFromEnd : "eliminar este elemento del principio del final del <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.RemoveItemFromIndex : "eliminar este elemento a partir del íncice <index> del <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.Clear : "borrar el <<<subject>>>",
}



##################
# Name Config
##################

nameMap = {
	"edu.cmu.cs.stage3.alice.core.responses.DoInOrder" : "hacer en orden",
	"edu.cmu.cs.stage3.alice.core.responses.DoTogether" : "hacer juntos",
	"edu.cmu.cs.stage3.alice.core.responses.IfElseInOrder" : "if/else",
	"edu.cmu.cs.stage3.alice.core.responses.LoopNInOrder" : "lazo",
	"edu.cmu.cs.stage3.alice.core.responses.WhileLoopInOrder" : "while",
	"edu.cmu.cs.stage3.alice.core.responses.ForEachInOrder" : "para todos en orden",
	"edu.cmu.cs.stage3.alice.core.responses.ForEachTogether" : "para todos juntos",
	"edu.cmu.cs.stage3.alice.core.responses.Print" : "imprimir",
	"edu.cmu.cs.stage3.alice.core.responses.QuaternionAnimation.quaternion" : "compensado por",
	"edu.cmu.cs.stage3.alice.core.responses.PointOfViewAnimation.pointOfView" : "punto de vista de",
	"edu.cmu.cs.stage3.alice.core.responses.PositionAnimation.position" : "posicion de",

	"edu.cmu.cs.stage3.alice.core.question.userdefined.Return" : "regresar",

	"edu.cmu.cs.stage3.alice.core.behaviors.WorldStartBehavior" : "Cuando el Mundo comienza",
	"edu.cmu.cs.stage3.alice.core.behaviors.WorldIsRunningBehavior" : "Cuando el Mundo se está ejecutando",
	"edu.cmu.cs.stage3.alice.core.behaviors.KeyClickBehavior" : "Cuando se presiona <keyCode>",
	"edu.cmu.cs.stage3.alice.core.behaviors.KeyIsPressedBehavior" : "Mientras <keyCode> es apretado",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonClickBehavior" : "Cuando se hace clic con el <mouse> en <onWhat>",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonIsPressedBehavior" : "Mientras el botón del ratón <mouse> se aprieta en <onWhat>",
	"edu.cmu.cs.stage3.alice.core.behaviors.ConditionalBehavior" : "Mientras <condition> sea verdadero",
	"edu.cmu.cs.stage3.alice.core.behaviors.ConditionalTriggerBehavior" : "Cuando <condition> se convierte en verdadero",
	"edu.cmu.cs.stage3.alice.core.behaviors.VariableChangeBehavior" : "Cuando <variable> cambia",
	"edu.cmu.cs.stage3.alice.core.behaviors.MessageReceivedBehavior" : "Cuando un mensaje es enviado de <fromWho> a <toWhom>", 
	"edu.cmu.cs.stage3.alice.core.behaviors.DefaultMouseInteractionBehavior" : "Permitir que el <mouse> mueva <objects>",
	"edu.cmu.cs.stage3.alice.core.behaviors.KeyboardNavigationBehavior" : "Permitir que <arrowKeys> mueva <subject>",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseNavigationBehavior" : "Permitir que el <mouse> mueva la cámara",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseLookingBehavior" : "Permitir que el <mouse> oriente la cámara",
	"edu.cmu.cs.stage3.alice.core.behaviors.SoundMarkerPassedBehavior" : "Cuando el sonido <marker> se reproduce",
	"edu.cmu.cs.stage3.alice.core.behaviors.SoundLevelBehavior" : "Cuando el nivel de grabación del sonido es >= <level>",

	"edu.cmu.cs.stage3.alice.core.Model.opacity" : "opacidad",
	"edu.cmu.cs.stage3.alice.core.Model.diffuseColorMap" : "textura de la piel",
	"diffuseColorMap" : "textura de la piel",
	"edu.cmu.cs.stage3.alice.core.Transformable.localTransformation" : "cuaternion",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonClickBehavior.onWhat" : "encimaDeQué",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonIsPressedBehavior.onWhat" : "encimaDeQué",
	"edu.cmu.cs.stage3.alice.core.question.IsCloseTo.threshold" : "está dentro de",
	"edu.cmu.cs.stage3.alice.core.question.IsFarFrom.threshold" : "es por lo menos",
	"edu.cmu.cs.stage3.alice.core.question.IsCloseTo.object" : "de",
	"edu.cmu.cs.stage3.alice.core.question.IsFarFrom.object" : "lejos de",

	"edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Renderer" : "DirectX 7",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.openglrenderer.Renderer" : "OpenGL",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.java3drenderer.Renderer" : "Java3D",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.Renderer" : "JOGL",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer.Renderer" : "Ninguno",

	edu.cmu.cs.stage3.alice.core.styles.TraditionalAnimationStyle.BEGIN_AND_END_GENTLY : "comenzar y terminar suavemente",
	edu.cmu.cs.stage3.alice.core.styles.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY : "comenzar y terminar abruptmente",
	edu.cmu.cs.stage3.alice.core.styles.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY : "comenzr abruptamenta y terminar suavemente",
	edu.cmu.cs.stage3.alice.core.styles.TraditionalAnimationStyle.BEGIN_AND_END_ABRUPTLY : "comenzar y terminar abruptamente",

	edu.cmu.cs.stage3.alice.core.Direction.LEFT : "izquierda",
	edu.cmu.cs.stage3.alice.core.Direction.RIGHT : "derecha",
	edu.cmu.cs.stage3.alice.core.Direction.UP : "arriba",
	edu.cmu.cs.stage3.alice.core.Direction.DOWN : "abajo",
	edu.cmu.cs.stage3.alice.core.Direction.FORWARD : "adelante",
	edu.cmu.cs.stage3.alice.core.Direction.BACKWARD : "atrás",

	edu.cmu.cs.stage3.alice.core.SpatialRelation.LEFT_OF : "a la izquierda de",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF : "a la derecha de",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.ABOVE : "encima",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BELOW : "debajo",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF : "en frente de",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND : "detrás",

	edu.cmu.cs.stage3.alice.core.Dimension.ALL : "todo",
	edu.cmu.cs.stage3.alice.core.Dimension.LEFT_TO_RIGHT : "de izquierda a derecha",
	edu.cmu.cs.stage3.alice.core.Dimension.TOP_TO_BOTTOM : "de arriba hacia abajo",
	edu.cmu.cs.stage3.alice.core.Dimension.FRONT_TO_BACK : "de adelante hacia atrás",

	edu.cmu.cs.stage3.alice.core.FogStyle.NONE : "ninguno",
	edu.cmu.cs.stage3.alice.core.FogStyle.LINEAR : "linear",
	edu.cmu.cs.stage3.alice.core.FogStyle.EXPONENTIAL : "exponential",

	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.SOLID : "sólido",
 	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.WIREFRAME : "alambre",
	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.POINTS : "puntos",

	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.NONE : "ninguno",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.FLAT : "plano",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.SMOOTH : "suave",

	java.lang.Boolean : "Booleano",
	java.lang.Number : "Número",
	edu.cmu.cs.stage3.alice.core.Model : "Objeto",

	Boolean.TRUE : "verdadero",
	Boolean.FALSE : "falso",

	edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE : "blanco",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK : "negro",
	edu.cmu.cs.stage3.alice.scenegraph.Color.RED : "rojo",
	edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN : "verde",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE : "azul",
	edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW : "amarillo",
	edu.cmu.cs.stage3.alice.scenegraph.Color.PURPLE : "morado",
	edu.cmu.cs.stage3.alice.scenegraph.Color.ORANGE : "anaranjado",
	edu.cmu.cs.stage3.alice.scenegraph.Color.PINK : "rosado",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BROWN : "marrón",
	edu.cmu.cs.stage3.alice.scenegraph.Color.CYAN : "aguamarina",
	edu.cmu.cs.stage3.alice.scenegraph.Color.MAGENTA : "púrpura",
	edu.cmu.cs.stage3.alice.scenegraph.Color.GRAY : "gris",
	edu.cmu.cs.stage3.alice.scenegraph.Color.LIGHT_GRAY : "gris claro",
	edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY : "gris oscuro",

	edu.cmu.cs.stage3.util.HowMuch.INSTANCE : "la instancia",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS : "la instancia y las partes",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS : "lainstancia y todos los descendientes",
}

htmlNameMap = {
	"edu.cmu.cs.stage3.alice.core.Transformable" : "[Obj]",
	"edu.cmu.cs.stage3.alice.core.Model" : "[Obj]",
	"java.lang.Number" : "[123]",
	"java.lang.Boolean" : "[T/F]",
	"java.lang.String" : "[ABC]",
	"edu.cmu.cs.stage3.alice.scenegraph.Color" : "[Color]",
	"edu.cmu.cs.stage3.alice.core.TextureMap" : "[Textura]",
	"edu.cmu.cs.stage3.alice.core.Sound" : "[Sonido]",
	"edu.cmu.cs.stage3.alice.core.Pose" : "[Pose]",
	"edu.cmu.cs.stage3.math.Vector3" : "[Posicion]",
	"edu.cmu.cs.stage3.math.Quaternion" : "[Ori]",
	"edu.cmu.cs.stage3.math.Matrix44" : "[POV]",
	"edu.cmu.cs.stage3.alice.core.ReferenceFrame" : "[Objeto]",
	"edu.cmu.cs.stage3.alice.core.Light" : "[Luz]",
	"edu.cmu.cs.stage3.alice.core.Direction" : "[Dirección]",
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
	"stdErrTextColor" : java.awt.Color( 138, 212, 101 ),
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
