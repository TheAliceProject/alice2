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
	edu.cmu.cs.stage3.alice.core.responses.MoveAnimation : "<<<subject>>> mova <<direction>><<amount>>",
	edu.cmu.cs.stage3.alice.core.responses.MoveTowardAnimation : "<<<subject>>> mova <<amount>> para <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.MoveAwayFromAnimation : "<<<subject>>> mova <<amount>> a partir de <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnAnimation : "<<<subject>>> gire <<direction>><<amount>>",
	edu.cmu.cs.stage3.alice.core.responses.RollAnimation : "<<<subject>>> role <<direction>><<amount>>",
	edu.cmu.cs.stage3.alice.core.responses.MoveAtSpeed : "<<<subject>>> mova na velocidade <<direction>><<speed>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnAtSpeed : "<<<subject>>> gire na velocidade <<direction>><<speed>>",
	edu.cmu.cs.stage3.alice.core.responses.RollAtSpeed : "<<<subject>>> role na velocidade <<direction>><<speed>>",
	edu.cmu.cs.stage3.alice.core.responses.ResizeAnimation : "<<<subject>>> redimensione <<amount>>",
	edu.cmu.cs.stage3.alice.core.responses.PointAtAnimation : "<<<subject>>> aponte para <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnToFaceAnimation : "<<<subject>>> gire para a face <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnAwayFromAnimation : "<<<subject>>> gire se afastando de <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.PointAtConstraint : "<<<subject>>> restringido para apontar <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnToFaceConstraint : "<<<subject>>> restringido para olhar de frente <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.TurnAwayFromConstraint : "<<<subject>>> restringido a virar para <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.GetAGoodLookAtAnimation : "<<<subject>>> obtenha uma boa visão de <<target>>",
	edu.cmu.cs.stage3.alice.core.responses.StandUpAnimation : "<<<subject>>> levante",
	edu.cmu.cs.stage3.alice.core.responses.PositionAnimation : "<<<subject>>> mova para <<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.responses.PlaceAnimation : "<<<subject>>> caitlin move para <<amount>><<spatialRelation>><<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.responses.QuaternionAnimation : "<<<subject>>> oriente para <<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.responses.PointOfViewAnimation : "<<<subject>>> defina ponto de vista para <<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.responses.PropertyAnimation : "<<<element>>> defina <propertyName> para <<value>>",
	edu.cmu.cs.stage3.alice.core.responses.SoundResponse : "<<<subject>>> reproduza o som <<sound>>",
	edu.cmu.cs.stage3.alice.core.responses.Wait : "Espere <<duration>>",
	edu.cmu.cs.stage3.alice.core.responses.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.responses.Print : "mostre <<text>> <<object>>",
	edu.cmu.cs.stage3.alice.core.responses.CallToUserDefinedResponse : "<userDefinedResponse><requiredActualParameters>",
	edu.cmu.cs.stage3.alice.core.responses.ScriptResponse : "Roteiro <<script>>",
	edu.cmu.cs.stage3.alice.core.responses.ScriptDefinedResponse : "Resposta Definida por Roteiro <<script>>",
	edu.cmu.cs.stage3.alice.core.responses.SayAnimation : "<<<subject>>> diga <<what>>",
	edu.cmu.cs.stage3.alice.core.responses.ThinkAnimation : "<<<subject>>> pense <<what>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse : "animação de quadro chave por posição <<subject>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse : "animação de quadro chave por orientação <<subject>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse : "animação de quadro chave por escala <<subject>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse : "animação de quadro chave <<subject>>",
	edu.cmu.cs.stage3.alice.core.responses.PoseAnimation : "<<<subject>>> defina pose <<pose>>",
	edu.cmu.cs.stage3.alice.core.responses.Increment : "incremente <<<variable>>> por 1",
	edu.cmu.cs.stage3.alice.core.responses.Decrement : "decremente <<<variable>>> por 1",

	edu.cmu.cs.stage3.alice.core.responses.VehiclePropertyAnimation : "<element> defina <propertyName> para <value>",

	edu.cmu.cs.stage3.alice.core.responses.list.InsertItemAtBeginning : "adicione <item> no início de <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.InsertItemAtEnd : "adicione <item> no final de <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.InsertItemAtIndex : "adicione <item> na posição <index> de <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.RemoveItemFromBeginning : "adicione item do início de <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.RemoveItemFromEnd : "remova item do final de <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.RemoveItemFromIndex : "remova item da posição <index> de <<<list>>>",
	edu.cmu.cs.stage3.alice.core.responses.list.Clear : "remova todos itens de <<<list>>>",

	edu.cmu.cs.stage3.alice.core.responses.array.SetItemAtIndex : "defina item <index> para <item> em <<<array>>>",

	edu.cmu.cs.stage3.alice.core.responses.vector3.SetX : "defina distância de <<<vector3>>> à direita <<value>>",
	edu.cmu.cs.stage3.alice.core.responses.vector3.SetY : "defina distância de <<<vector3>>> acima <<value>>",
	edu.cmu.cs.stage3.alice.core.responses.vector3.SetZ : "defina distância de <<<vector3>>> a frente <<value>>",

	edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion : "<userDefinedQuestion><requiredActualParameters>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Return : "Devolva <<value>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Print : "mostre <<text>> <<object>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment : "<element> defina <propertyName> para <value>",

	edu.cmu.cs.stage3.alice.core.question.PartKeyed : "Parte de <<<owner>>> nomeada <key>",

	edu.cmu.cs.stage3.alice.core.question.Width : "Largura de <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.question.Height : "Altura de <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.question.Depth : "Profundidade de <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.question.Quaternion : "Quaternion <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.question.Position : "Posição de <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.question.PointOfView : "Ponto de vista <<<subject>>>",

	edu.cmu.cs.stage3.alice.core.question.Not : "Não <a>",
	edu.cmu.cs.stage3.alice.core.question.And : "ambos <a> e <b>",
	edu.cmu.cs.stage3.alice.core.question.Or : "qualquer <a> ou <b>, ou ambos",

	edu.cmu.cs.stage3.alice.core.question.StringConcatQuestion : "<a> junto com <b>",
	edu.cmu.cs.stage3.alice.core.question.ToStringQuestion : "<what> como uma cadeia de caracteres",

	edu.cmu.cs.stage3.alice.core.question.StringToUpperCaseQuestion : "<a> to uppercase",
	edu.cmu.cs.stage3.alice.core.question.StringToLowerCaseQuestion : "<a> to lowercase",

	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber : "peça ao usuário um número <<question>>",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo : "peça ao usuário sim ou não <<question>>",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString : "peça ao usuário uma cadeia de caracteres <<question>>",

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

	edu.cmu.cs.stage3.alice.core.question.math.Min : "mínimo de <a> e <b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Max : "máximo de <a> e <b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Abs : "valor absoluto de <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Sqrt : "raiz quadrada de <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Floor : "maior inteiro não maior que <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Ceil : "o menor inteiro não menor que <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Sin : "sen <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Cos : "cos <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Tan : "tan <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ASin : "arcsen <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ACos : "arccos <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ATan : "arctan <a>",
	edu.cmu.cs.stage3.alice.core.question.math.ATan2 : "arctan2 <a><b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Pow : "<a> elevado a <b> potência", 
	edu.cmu.cs.stage3.alice.core.question.math.Log : "log natural de <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Exp : "e elevado a <a> potência", 
	edu.cmu.cs.stage3.alice.core.question.math.IEEERemainder : "Resto resultante de <a>/<b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Int : "int <a>",
	edu.cmu.cs.stage3.alice.core.question.math.Round : "arredondado <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ToDegrees : "<a> convertido de radianos para graus", 
	edu.cmu.cs.stage3.alice.core.question.math.ToRadians : "<a> convertido de graus para radianos", 
	edu.cmu.cs.stage3.alice.core.question.math.SuperSqrt : "a <b>a. raiz de <a>",

	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromLeftEdge : "distância do mouse até a margem esquerda", 
	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromTopEdge : "distância do mouse até a margem superior", 

	edu.cmu.cs.stage3.alice.core.question.time.TimeElapsedSinceWorldStart : "tempo decorrido", 

	edu.cmu.cs.stage3.alice.core.question.time.Year : "ano", 
	edu.cmu.cs.stage3.alice.core.question.time.MonthOfYear : "mês do ano", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfYear : "dia do ano", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfMonth : "dia do mês", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeek : "dia da semana", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeekInMonth : "dia da semana no mês", 
	edu.cmu.cs.stage3.alice.core.question.time.IsAM : "é AM", 
	edu.cmu.cs.stage3.alice.core.question.time.IsPM : "é PM", 
	edu.cmu.cs.stage3.alice.core.question.time.HourOfAMOrPM : "hora de AM ou PM", 
	edu.cmu.cs.stage3.alice.core.question.time.HourOfDay : "hora do dia", 
	edu.cmu.cs.stage3.alice.core.question.time.MinuteOfHour : "minuto da hora", 
	edu.cmu.cs.stage3.alice.core.question.time.SecondOfMinute : "segundo do minuto", 

	edu.cmu.cs.stage3.alice.core.question.RandomBoolean : "escolha verdadeira <probabilityOfTrue> do tempo",
	edu.cmu.cs.stage3.alice.core.question.RandomNumber : "número randômico",

	edu.cmu.cs.stage3.alice.core.question.list.Contains : "<list> contém <item>",
	edu.cmu.cs.stage3.alice.core.question.list.FirstIndexOfItem : "primeiro índice do <item> de <list>",
	edu.cmu.cs.stage3.alice.core.question.list.IsEmpty : "<list> está vazia",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtBeginning : "primeiro item de <list>",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtEnd : "último item de <list>",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex : "item <index> de <list>",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtRandomIndex : "item randômico de <list>",
	edu.cmu.cs.stage3.alice.core.question.list.LastIndexOfItem : "último índice de <item> de <list>",
	edu.cmu.cs.stage3.alice.core.question.list.Size : "tamanho de <list>",

	edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex : "item <index> de <<<array>>>",
	edu.cmu.cs.stage3.alice.core.question.array.Size : "tamanho de <<<array>>>",

	edu.cmu.cs.stage3.alice.core.question.IsAbove : "<<<subject>>> está acima <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsBehind : "<<<subject>>> está atrás <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsBelow : "<<<subject>>> está abaixo <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsInFrontOf : "<<<subject>>> está em frente de <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsLeftOf : "<<<subject>>> está à esquerda de <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsRightOf : "<<<subject>>> está à direita de <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsSmallerThan : "<<<subject>>> é menor que <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsLargerThan : "<<<subject>>> é maior que <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsNarrowerThan : "<<<subject>>> é mais estreito que <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsWiderThan : "<<<subject>>> é mais amplo que <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsShorterThan : "<<<subject>>> é mais curto que <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsTallerThan : "<<<subject>>> é mais alto que <<object>>",
 
	edu.cmu.cs.stage3.alice.core.question.IsCloseTo : "<<<subject>>> está dentro <threshold> de <object>",
	edu.cmu.cs.stage3.alice.core.question.IsFarFrom : "<<<subject>>> está pelo menos <threshold> longe de <object>",
	edu.cmu.cs.stage3.alice.core.question.DistanceTo : "<<<subject>>> distância para <<object>>",

	edu.cmu.cs.stage3.alice.core.question.DistanceToTheLeftOf : "<<<subject>>> distância a esquerda de <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceToTheRightOf : "<<<subject>>> distância a direita de <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceAbove : "<<<subject>>> distância acima de <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceBelow : "<<<subject>>> distância abaixo de <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceInFrontOf : "<<<subject>>> distância na frente de <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceBehind : "<<<subject>>> distância atrás <<object>>",

	edu.cmu.cs.stage3.alice.core.question.vector3.X : "distância direita de <<<vector3>>>",
	edu.cmu.cs.stage3.alice.core.question.vector3.Y : "distância acima de <<<vector3>>>",
	edu.cmu.cs.stage3.alice.core.question.vector3.Z : "distância a frente de <<<vector3>>>",

	edu.cmu.cs.stage3.alice.core.question.PickQuestion : "objeto sob o cursor do mouse",

	edu.cmu.cs.stage3.alice.core.question.RightUpForward : "<right>, <up>, <forward>",

	edu.cmu.cs.stage3.alice.core.question.Pose : "Pose atual de <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.question.PropertyValue : "<<<element>>>.<propertyName>",

	edu.cmu.cs.stage3.alice.core.question.visualization.model.Item : "o valor de <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.model.SetItem : "deixe <<<subject>>> = <item>",

	edu.cmu.cs.stage3.alice.core.question.visualization.array.ItemAtIndex : "o valor de <<<subject>>>[ <index> ]",
	edu.cmu.cs.stage3.alice.core.responses.visualization.array.SetItemAtIndex : "deixe <<<subject>>>[ <index> ] = <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.array.Size : "tamanho do <<<subject>>>",

	edu.cmu.cs.stage3.alice.core.question.visualization.list.Size : "tamanho do <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.Contains : "<<<subject>>> contém <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.IsEmpty : "<<<subject>>> está vazio",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.FirstIndexOfItem : "<<<subject>>> é o primeiro índice do <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.LastIndexOfItem : "<<<subject>>> é o último índice do <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtBeginning : "<<<subject>>> é o item no início",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtEnd : "<<<subject>>> é o item no final",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtIndex : "<<<subject>>> é o item no índice <index>",
	
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.InsertItemAtBeginning : "adicione <item> no início de <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.InsertItemAtEnd : "adicione <item> no final de <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.InsertItemAtIndex : "adicione <item> no <index> de <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.RemoveItemFromBeginning : "remova um item do início de <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.RemoveItemFromEnd : "remova um item do final de <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.RemoveItemFromIndex : "remova um item do <index> de <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.responses.visualization.list.Clear : "limpe <<<subject>>>",
}



##################
# Name Config
##################

nameMap = {
	"edu.cmu.cs.stage3.alice.core.responses.DoInOrder" : "Faça em ordem",
	"edu.cmu.cs.stage3.alice.core.responses.DoTogether" : "Faça junto",
	"edu.cmu.cs.stage3.alice.core.responses.IfElseInOrder" : "Se/Senão",
	"edu.cmu.cs.stage3.alice.core.responses.LoopNInOrder" : "Repita",
	"edu.cmu.cs.stage3.alice.core.responses.WhileLoopInOrder" : "Enquanto",
	"edu.cmu.cs.stage3.alice.core.responses.ForEachInOrder" : "Para todos em ordem",
	"edu.cmu.cs.stage3.alice.core.responses.ForEachTogether" : "Para todos juntos",
	"edu.cmu.cs.stage3.alice.core.responses.Print" : "Mostre",
	"edu.cmu.cs.stage3.alice.core.responses.QuaternionAnimation.quaternion" : "deslocamento por",
	"edu.cmu.cs.stage3.alice.core.responses.PointOfViewAnimation.pointOfView" : "ponto de vista de",
	"edu.cmu.cs.stage3.alice.core.responses.PositionAnimation.position" : "deslocamento por",

	"edu.cmu.cs.stage3.alice.core.question.userdefined.Return" : "Devolve",

	"edu.cmu.cs.stage3.alice.core.behaviors.WorldStartBehavior" : "Quando o mundo começa",
	"edu.cmu.cs.stage3.alice.core.behaviors.WorldIsRunningBehavior" : "Enquanto o mundo está executando",
	"edu.cmu.cs.stage3.alice.core.behaviors.KeyClickBehavior" : "Quando <keyCode> é digitado",
	"edu.cmu.cs.stage3.alice.core.behaviors.KeyIsPressedBehavior" : "Enquanto <keyCode> é pressionado",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonClickBehavior" : "Quando <mouse> é clicado sobre <onWhat>",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonIsPressedBehavior" : "Enquanto <mouse> é pressionado sobre <onWhat>",
	"edu.cmu.cs.stage3.alice.core.behaviors.ConditionalBehavior" : "Enquanto <condition> é verdadeira",
	"edu.cmu.cs.stage3.alice.core.behaviors.ConditionalTriggerBehavior" : "Quando <condition> se torna verdadeira",
	"edu.cmu.cs.stage3.alice.core.behaviors.VariableChangeBehavior" : "Quando <variable> muda",
	"edu.cmu.cs.stage3.alice.core.behaviors.MessageReceivedBehavior" : "Quando uma mensagem é recebida por <toWhom> de <fromWho>", 
	"edu.cmu.cs.stage3.alice.core.behaviors.DefaultMouseInteractionBehavior" : "Deixe <mouse> mover <objects>",
	"edu.cmu.cs.stage3.alice.core.behaviors.KeyboardNavigationBehavior" : "Deixe <arrowKeys> mover <subject>",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseNavigationBehavior" : "Deixe <mouse> mover the camera",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseLookingBehavior" : "Deixe <mouse> orientar the camera",
	"edu.cmu.cs.stage3.alice.core.behaviors.SoundMarkerPassedBehavior" : "Quando som marcado <marker> é reproduzido",
	"edu.cmu.cs.stage3.alice.core.behaviors.SoundLevelBehavior" : "Quando o nível de gravação de som é >= <level>",

	"edu.cmu.cs.stage3.alice.core.Model.opacity" : "opacidade",
	"edu.cmu.cs.stage3.alice.core.Model.diffuseColorMap" : "textura da pele",
	"diffuseColorMap" : "textura da pele",
	"edu.cmu.cs.stage3.alice.core.Transformable.localTransformation" : "pontoDeVista",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonClickBehavior.onWhat" : "emQue",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonIsPressedBehavior.onWhat" : "emQue",
	"edu.cmu.cs.stage3.alice.core.question.IsCloseTo.threshold" : "está dentro",
	"edu.cmu.cs.stage3.alice.core.question.IsFarFrom.threshold" : "está pelo menos",
	"edu.cmu.cs.stage3.alice.core.question.IsCloseTo.object" : "de",
	"edu.cmu.cs.stage3.alice.core.question.IsFarFrom.object" : "distância a partir de",

	"edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Renderer" : "DirectX 7",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.openglrenderer.Renderer" : "OpenGL",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.java3drenderer.Renderer" : "Java3D",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.Renderer" : "JOGL",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer.Renderer" : "None",

	edu.cmu.cs.stage3.alice.core.styles.TraditionalAnimationStyle.BEGIN_AND_END_GENTLY : "suavemente",
	edu.cmu.cs.stage3.alice.core.styles.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY : "inicia suavemente",
	edu.cmu.cs.stage3.alice.core.styles.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY : "finaliza suavemente",
	edu.cmu.cs.stage3.alice.core.styles.TraditionalAnimationStyle.BEGIN_AND_END_ABRUPTLY : "bruscamente",

	edu.cmu.cs.stage3.alice.core.Direction.LEFT : "esquerda",
	edu.cmu.cs.stage3.alice.core.Direction.RIGHT : "direita",
	edu.cmu.cs.stage3.alice.core.Direction.UP : "cima",
	edu.cmu.cs.stage3.alice.core.Direction.DOWN : "baixo",
	edu.cmu.cs.stage3.alice.core.Direction.FORWARD : "frente",
	edu.cmu.cs.stage3.alice.core.Direction.BACKWARD : "atrás",

	edu.cmu.cs.stage3.alice.core.SpatialRelation.LEFT_OF : "esquerda de",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF : "direita de ",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.ABOVE : "acima",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BELOW : "abaixo",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF : "em frente de",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND : "atrás",

	edu.cmu.cs.stage3.alice.core.Dimension.ALL : "tudo",
	edu.cmu.cs.stage3.alice.core.Dimension.LEFT_TO_RIGHT : "esquerda para direita",
	edu.cmu.cs.stage3.alice.core.Dimension.TOP_TO_BOTTOM : "topo para base",
	edu.cmu.cs.stage3.alice.core.Dimension.FRONT_TO_BACK : "frente para atrás",

	edu.cmu.cs.stage3.alice.core.FogStyle.NONE : "nenhum fog",
	edu.cmu.cs.stage3.alice.core.FogStyle.LINEAR : "distância",
	edu.cmu.cs.stage3.alice.core.FogStyle.EXPONENTIAL : "densidade",

	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.SOLID : "sólido",
 	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.WIREFRAME : "representação aramada",
	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.POINTS : "pontos",

	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.NONE : "nenhum",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.FLAT : "plano",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.SMOOTH : "liso",

	java.lang.Boolean : "Booleano",
	java.lang.Number : "Número",
	edu.cmu.cs.stage3.alice.core.Model : "Objeto",

	Boolean.TRUE : "verdadeiro",
	Boolean.FALSE : "falso",

	edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE : "branco",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK : "preto",
	edu.cmu.cs.stage3.alice.scenegraph.Color.RED : "vermelho",
	edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN : "verde",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE : "azul",
	edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW : "amarelo",
	edu.cmu.cs.stage3.alice.scenegraph.Color.PURPLE : "roxo",
	edu.cmu.cs.stage3.alice.scenegraph.Color.ORANGE : "laranja",
	edu.cmu.cs.stage3.alice.scenegraph.Color.PINK : "rosa",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BROWN : "marrom",
	edu.cmu.cs.stage3.alice.scenegraph.Color.CYAN : "ciano",
	edu.cmu.cs.stage3.alice.scenegraph.Color.MAGENTA : "magenta",
	edu.cmu.cs.stage3.alice.scenegraph.Color.GRAY : "cinza",
	edu.cmu.cs.stage3.alice.scenegraph.Color.LIGHT_GRAY : "cinza claro",
	edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY : "cinza escuro",

	edu.cmu.cs.stage3.util.HowMuch.INSTANCE : "somento objeto",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS : "objeto e parte",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS : "objeto e todos descendentes",
}

htmlNameMap = {
	"edu.cmu.cs.stage3.alice.core.Transformable" : "[Obj]",
	"edu.cmu.cs.stage3.alice.core.Model" : "[Obj]",
	"java.lang.Number" : "[123]",
	"java.lang.Boolean" : "[T/F]",
	"java.lang.String" : "[ABC]",
	"edu.cmu.cs.stage3.alice.scenegraph.Color" : "[Cor]",
	"edu.cmu.cs.stage3.alice.core.TextureMap" : "[Textura]",
	"edu.cmu.cs.stage3.alice.core.Sound" : "[Som]",
	"edu.cmu.cs.stage3.alice.core.Pose" : "[Pose]",
	"edu.cmu.cs.stage3.math.Vector3" : "[Pos]",
	"edu.cmu.cs.stage3.math.Quaternion" : "[Ori]",
	"edu.cmu.cs.stage3.math.Matrix44" : "[POV]",
	"edu.cmu.cs.stage3.alice.core.ReferenceFrame" : "[Obj]",
	"edu.cmu.cs.stage3.alice.core.Light" : "[Luz]",
	"edu.cmu.cs.stage3.alice.core.Direction" : "[Direção]",
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

