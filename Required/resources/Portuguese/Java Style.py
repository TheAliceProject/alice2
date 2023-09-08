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
	edu.cmu.cs.stage3.alice.core.responses.MoveAnimation : "<<<subject>>>.mova( <direction>, <amount> );",
	edu.cmu.cs.stage3.alice.core.responses.MoveTowardAnimation : "<<<subject>>>.movaPara( <target>, <amount> );",
	edu.cmu.cs.stage3.alice.core.responses.MoveAwayFromAnimation : "<<<subject>>>.movaAfastandoDe( <target>, <amount> );",
	edu.cmu.cs.stage3.alice.core.responses.TurnAnimation : "<<<subject>>>.gire( <direction>, <amount> );",
	edu.cmu.cs.stage3.alice.core.responses.RollAnimation : "<<<subject>>>.role( <direction>, <amount> );",
	edu.cmu.cs.stage3.alice.core.responses.MoveAtSpeed : "<<<subject>>>.movaAUmaVelocidade( <direction>, <speed> );",
	edu.cmu.cs.stage3.alice.core.responses.TurnAtSpeed : "<<<subject>>>.gireAUmaVelocidade( <direction>, <speed> );",
	edu.cmu.cs.stage3.alice.core.responses.RollAtSpeed : "<<<subject>>>.roleAUmaVelocidade( <direction>, <speed> );",
	edu.cmu.cs.stage3.alice.core.responses.ResizeAnimation : "<<<subject>>>.redimensione( <amount> );",
	edu.cmu.cs.stage3.alice.core.responses.PointAtAnimation : "<<<subject>>>.apontePara( <target> );",
	edu.cmu.cs.stage3.alice.core.responses.TurnToFaceAnimation : "<<<subject>>>.gireParaAFace( <target> );",
	edu.cmu.cs.stage3.alice.core.responses.TurnAwayFromAnimation : "<<<subject>>>.gireAfastandoDe( <target> );",
	edu.cmu.cs.stage3.alice.core.responses.PointAtConstraint : "<<<subject>>>.restringidoParaApontar( <target> );",
	edu.cmu.cs.stage3.alice.core.responses.GetAGoodLookAtAnimation : "<<<subject>>>.obtenhaUmaBoaVisão( <target> );",
	edu.cmu.cs.stage3.alice.core.responses.StandUpAnimation : "<<<subject>>>.levante();",
	edu.cmu.cs.stage3.alice.core.responses.PositionAnimation : "<<<subject>>>.movaPara( <asSeenBy> );",
	edu.cmu.cs.stage3.alice.core.responses.PlaceAnimation : "<<<subject>>>.posicione( <amount>, <spatialRelation>, <asSeenBy> );",
	edu.cmu.cs.stage3.alice.core.responses.QuaternionAnimation : "<<<subject>>>.orientePara( <asSeenBy> );",
	edu.cmu.cs.stage3.alice.core.responses.PointOfViewAnimation : "<<<subject>>>.definaOPontoDeVista( <asSeenBy> );",
	edu.cmu.cs.stage3.alice.core.responses.PropertyAnimation : "<element>.defina( <propertyName>, <value> );",
	edu.cmu.cs.stage3.alice.core.responses.SoundResponse : "<<<subject>>>.reproduzaOSom( <sound> );",
	edu.cmu.cs.stage3.alice.core.responses.Wait : "espere( <duration> );",
	edu.cmu.cs.stage3.alice.core.responses.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.responses.Print : "mostre( <text>, <object> );",
	edu.cmu.cs.stage3.alice.core.responses.CallToUserDefinedResponse : "<userDefinedResponse>( <requiredActualParameters> );",
	edu.cmu.cs.stage3.alice.core.responses.ScriptResponse : "roteiro( <script> );",
	edu.cmu.cs.stage3.alice.core.responses.ScriptDefinedResponse : "respostaDefinidaPorRoteiro( <script> );",
	edu.cmu.cs.stage3.alice.core.responses.SayAnimation : "<<<subject>>>.diga( <what> );",
	edu.cmu.cs.stage3.alice.core.responses.ThinkAnimation : "<<<subject>>>.pense( <what> );",
	edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse : "executeAnimaçãoDoQuadroChavePorPosiçãoEm( <subject> );",
	edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse : "executeAnimaçãoDoQuadroChavePorOrientaçãoEm( <subject> );",
	edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse : "executeAnimaçãoDoQuadroChavePorEscalaEm( <subject> );",
	edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse : "executeAnimaçãoDoQuadroChaveEm( <subject> );",
	edu.cmu.cs.stage3.alice.core.responses.PoseAnimation : "<<<subject>>>.definaPose( <pose> );",
	edu.cmu.cs.stage3.alice.core.responses.Increment : "<<<variable>>>++",
	edu.cmu.cs.stage3.alice.core.responses.Decrement : "<<<variable>>>--",

	edu.cmu.cs.stage3.alice.core.responses.VehiclePropertyAnimation : "<element>.defina( <propertyName>, <value> );",

	edu.cmu.cs.stage3.alice.core.responses.list.InsertItemAtBeginning : "<<<list>>>.adicione( 0, <item> );",
	edu.cmu.cs.stage3.alice.core.responses.list.InsertItemAtEnd : "<<<list>>>.adicione( <item> );",
	edu.cmu.cs.stage3.alice.core.responses.list.InsertItemAtIndex : "<<<list>>>.adicione( <index>, <item> );",
	edu.cmu.cs.stage3.alice.core.responses.list.RemoveItemFromBeginning : "<<<list>>>.remova( 0 );",
	edu.cmu.cs.stage3.alice.core.responses.list.RemoveItemFromEnd : "<<<list>>>.removaÚltimo();", 
	edu.cmu.cs.stage3.alice.core.responses.list.RemoveItemFromIndex : "<<<list>>>.remova( <index> );",
	edu.cmu.cs.stage3.alice.core.responses.list.Clear : "<<<list>>>.limpe();",

	edu.cmu.cs.stage3.alice.core.responses.array.SetItemAtIndex : "<<<array>>>[<index>] = <item>;",

	edu.cmu.cs.stage3.alice.core.responses.vector3.SetX : "<<<vector3>>>.definaDistânciaADireita( <value> )",
	edu.cmu.cs.stage3.alice.core.responses.vector3.SetY : "<<<vector3>>>.definaDistânciaAcima( <value> )",
	edu.cmu.cs.stage3.alice.core.responses.vector3.SetZ : "<<<vector3>>>.definaDistânciaAFrente( <value> )",

	edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion : "<userDefinedQuestion>( <requiredActualParameters> )",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Return : "devolva <<value>>;",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Print : "mostre( <text>, <object> );",
	edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment : "<element>.defina( <propertyName>, <value> );",

	edu.cmu.cs.stage3.alice.core.question.PartKeyed : "<<<owner>>>.parteNomeada( <key> )",

	edu.cmu.cs.stage3.alice.core.question.Width : "<<<subject>>>.obtenhaLargura()",
	edu.cmu.cs.stage3.alice.core.question.Height : "<<<subject>>>.obtenhaAltura()",
	edu.cmu.cs.stage3.alice.core.question.Depth : "<<<subject>>>.obtenhaProfundidade()",
	edu.cmu.cs.stage3.alice.core.question.Quaternion : "<<<subject>>>.obtenhaQuaternion()",
	edu.cmu.cs.stage3.alice.core.question.Position : "<<<subject>>>.obtenhaPosição()",
	edu.cmu.cs.stage3.alice.core.question.PointOfView : "<<<subject>>>.obtenhaPontoDeVista()",

	edu.cmu.cs.stage3.alice.core.question.Not : "!<a>",
	edu.cmu.cs.stage3.alice.core.question.And : "(<a>&&<b>)",
	edu.cmu.cs.stage3.alice.core.question.Or : "(<a>||<b>)",

	edu.cmu.cs.stage3.alice.core.question.StringConcatQuestion : "<a>+<b>",
	edu.cmu.cs.stage3.alice.core.question.ToStringQuestion : "<what>.paraCadeiaDeCaracteres()",

	edu.cmu.cs.stage3.alice.core.question.StringToUpperCaseQuestion : "<a>.toUpperCase",
	edu.cmu.cs.stage3.alice.core.question.StringToLowerCaseQuestion : "<a>.toLowerCase",

	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber : "DiálogoParaPedirNúmero(<question>)",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo : "DiálogoParaPedirBooleano(<question>)",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString : "DiálogoParaPedirCadeiaDeCaracteres(<question>)",

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

	edu.cmu.cs.stage3.alice.core.question.math.Min : "Mat.min( <a>, <b> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Max : "Mat.max( <a>, <b> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Abs : "Mat.abs( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Sqrt : "Mat.sqrt( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Floor : "Mat.floor( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Ceil : "Mat.ceil( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Sin : "Mat.sen( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Cos : "Mat.cos( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Tan : "Mat.tan( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.ASin : "Mat.asen( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.ACos : "Mat.acos( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.ATan : "Mat.atan( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.ATan2 : "Mat.atan2( <a>, <b> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Pow : "Mat.pow( <a>, <b> )",
	edu.cmu.cs.stage3.alice.core.question.math.Log : "Mat.log natural de <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Exp : "Mat.exp( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.IEEERemainder : "Mat.IEEERemainder( <a>, <b> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Round : "Mat.arredondado( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.ToDegrees : "Mat.paraGraus( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.ToRadians : "Mat.paraRadianos( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.SuperSqrt : "superRaizQuadrada( <a>, <b> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Int : "CadeiaDeCaracteres.valorDe( (int) <a> )",

	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromLeftEdge : "Mouse.obtenhaADistânciaDaMargemEsquerda()", 
	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromTopEdge : "Mouse.obtenhaADistânciaDaMargemSuperior()", 

	edu.cmu.cs.stage3.alice.core.question.time.TimeElapsedSinceWorldStart : "obtenhaTempoDecorridoDesdeInícioDoMundo()", 

	edu.cmu.cs.stage3.alice.core.question.time.Year : "obtenhaAno()", 
	edu.cmu.cs.stage3.alice.core.question.time.MonthOfYear : "obtenhaMêsDoAno()", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfYear : "obtenhaDiaDoAno()", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfMonth : "obtenhaDiaDoMês()", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeek : "obtenhaDiaDaSemana()", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeekInMonth : "obtenhaDiaDaSemanaNoMês()", 
	edu.cmu.cs.stage3.alice.core.question.time.IsAM : "éAM()", 
	edu.cmu.cs.stage3.alice.core.question.time.IsPM : "éPM()", 
	edu.cmu.cs.stage3.alice.core.question.time.HourOfAMOrPM : "obtenhaHoraAMOuPM()", 
	edu.cmu.cs.stage3.alice.core.question.time.HourOfDay : "obtenhaHoraDoDia()", 
	edu.cmu.cs.stage3.alice.core.question.time.MinuteOfHour : "obtenhaMinutoDaHora()", 
	edu.cmu.cs.stage3.alice.core.question.time.SecondOfMinute : "obtenhaSegundoDoMinuto()", 

	edu.cmu.cs.stage3.alice.core.question.RandomBoolean : "Randômico.próximoBooleano()",
	edu.cmu.cs.stage3.alice.core.question.RandomNumber : "Randômico.próximoReal()",

	edu.cmu.cs.stage3.alice.core.question.list.Contains : "<list>.contém( <item> )",
	edu.cmu.cs.stage3.alice.core.question.list.FirstIndexOfItem : "<list>.índiceDe( <item> )",
	edu.cmu.cs.stage3.alice.core.question.list.IsEmpty : "<list>.estáVazio()",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtBeginning : "<list>[0]",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtEnd : "<list>.obtenhaÚltimoItem()",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex : "<list>[<index>]",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtRandomIndex : "<list>.obtenhaItemRandômico()",
	edu.cmu.cs.stage3.alice.core.question.list.LastIndexOfItem : "<list>.últimoÍndiceDe( <item> )",
	edu.cmu.cs.stage3.alice.core.question.list.Size : "<list>.tamanho()",

	edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex : "<<<array>>>[<index>]",
	edu.cmu.cs.stage3.alice.core.question.array.Size : "<<<array>>>.tamanho",

	edu.cmu.cs.stage3.alice.core.question.IsAbove : "<<<subject>>>.estáAcima( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsBehind : "<<<subject>>>.estáAtrás( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsBelow : "<<<subject>>>.estáAbaixo( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsInFrontOf : "<<<subject>>>.estáNaFrenteDe( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsLeftOf : "<<<subject>>>.estáAEsquerdaDe( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsRightOf : "<<<subject>>>.estáADireitaDe( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsSmallerThan : "<<<subject>>>.éMenorQue( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsLargerThan : "<<<subject>>>.éMaiorQue( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsNarrowerThan : "<<<subject>>>.éMaisEstreitoQue( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsWiderThan : "<<<subject>>>.éMaisAmploQue( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsShorterThan : "<<<subject>>>.éMaisCurtoQue( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsTallerThan : "<<<subject>>>.éMaisAltoQue( <object> )",
 
	edu.cmu.cs.stage3.alice.core.question.IsCloseTo : "<<<subject>>>.estáPróximoA( <threshold>, <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsFarFrom : "<<<subject>>>.estáLongeDe( <threshold>, <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceTo : "<<<subject>>>.distânciaPara( <object> )",

	edu.cmu.cs.stage3.alice.core.question.DistanceToTheLeftOf : "<<<subject>>>.distânciaParaAEsquerdaDe( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceToTheRightOf : "<<<subject>>>.distânciaParaADireitaDe( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceAbove : "<<<subject>>>.distânciaAcima( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceBelow : "<<<subject>>>.distânciaAbaixo( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceInFrontOf : "<<<subject>>>.distânceInFrontOf( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceBehind : "<<<subject>>>.distânciaAtrás( <object> )",

	edu.cmu.cs.stage3.alice.core.question.vector3.X : "<<<vector3>>>.obtenhaDistânciaDireita()",
	edu.cmu.cs.stage3.alice.core.question.vector3.Y : "<<<vector3>>>.obtenhaDistânciaAcima()",
	edu.cmu.cs.stage3.alice.core.question.vector3.Z : "<<<vector3>>>.obtenhaDistânciaParaAFrente()",

	edu.cmu.cs.stage3.alice.core.question.PickQuestion : "oQueFoiEscolhido()",

	edu.cmu.cs.stage3.alice.core.question.RightUpForward : "obtenhaVetor( <right>, <up>, <forward> )",

	edu.cmu.cs.stage3.alice.core.question.Pose : "<<<subject>>>.obtenhaPoseAtual()",
}



##################
# Name Config
##################

nameMap = {
	"edu.cmu.cs.stage3.alice.core.responses.DoInOrder" : "façaEmOrdem",
	"edu.cmu.cs.stage3.alice.core.responses.DoTogether" : "façaJunto",
	"edu.cmu.cs.stage3.alice.core.responses.IfElseInOrder" : "se",
	"edu.cmu.cs.stage3.alice.core.responses.LoopNInOrder" : "repita",
	"edu.cmu.cs.stage3.alice.core.responses.WhileLoopInOrder" : "enquanto",
	"edu.cmu.cs.stage3.alice.core.responses.ForEachInOrder" : "paraTodosEmOrdem",
	"edu.cmu.cs.stage3.alice.core.responses.ForEachTogether" : "paraTodosJunto",
	"edu.cmu.cs.stage3.alice.core.responses.Print" : "mostre",
	"edu.cmu.cs.stage3.alice.core.responses.QuaternionAnimation.quaternion" : "orientação de",
	"edu.cmu.cs.stage3.alice.core.responses.PointOfViewAnimation.pointOfView" : "ponto de vista de",
	"edu.cmu.cs.stage3.alice.core.responses.PositionAnimation.position" : "posição de",

	"edu.cmu.cs.stage3.alice.core.question.userdefined.Return" : "devolve",

	"edu.cmu.cs.stage3.alice.core.behaviors.WorldStartBehavior" : "Quando o mundo inicia",
	"edu.cmu.cs.stage3.alice.core.behaviors.WorldIsRunningBehavior" : "Enquanto o mundo está executando",
	"edu.cmu.cs.stage3.alice.core.behaviors.KeyClickBehavior" : "Quando <keyCode> é digitado",
	"edu.cmu.cs.stage3.alice.core.behaviors.KeyIsPressedBehavior" : "Enquanto <keyCode> é pressionado",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonClickBehavior" : "Quando <mouse> é clicado sobre <onWhat>",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonIsPressedBehavior" : "Enquanto <mouse> é pressionado <onWhat>",
	"edu.cmu.cs.stage3.alice.core.behaviors.ConditionalBehavior" : "Enquanto <condition> é verdadeira",
	"edu.cmu.cs.stage3.alice.core.behaviors.ConditionalTriggerBehavior" : "Quando <condition> se torna verdadeira",
	"edu.cmu.cs.stage3.alice.core.behaviors.VariableChangeBehavior" : "Quando <variable> muda",
	"edu.cmu.cs.stage3.alice.core.behaviors.MessageReceivedBehavior" : "Quando uma mensagem é recebida por <toWhom> de <fromWho>", 
	"edu.cmu.cs.stage3.alice.core.behaviors.DefaultMouseInteractionBehavior" : "Deixe <mouse> mover <objects>",
	"edu.cmu.cs.stage3.alice.core.behaviors.KeyboardNavigationBehavior" : "Deixe <arrowKeys> mover <subject>",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseNavigationBehavior" : "Deixe <mouse> mover a câmera",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseLookingBehavior" : "Deixer <mouse> orientar a câmera",
	"edu.cmu.cs.stage3.alice.core.behaviors.SoundMarkerPassedBehavior" : "Quando o som marcado <marker> é tocado",
	"edu.cmu.cs.stage3.alice.core.behaviors.SoundLevelBehavior" : "Quando o nível de gravação do som é >= <level>",

	"edu.cmu.cs.stage3.alice.core.Model.opacity" : "opacidade",
	"edu.cmu.cs.stage3.alice.core.Model.diffuseColorMap" : "textura da pele",
	"diffuseColorMap" : "textura da pele",
	"edu.cmu.cs.stage3.alice.core.Transformable.localTransformation" : "pontoDeVista",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonClickBehavior.onWhat" : "emQue",
	"edu.cmu.cs.stage3.alice.core.behaviors.MouseButtonIsPressedBehavior.onWhat" : "emQue",
	"edu.cmu.cs.stage3.alice.core.question.IsCloseTo.threshold" : "está dentro",
	"edu.cmu.cs.stage3.alice.core.question.IsFarFrom.threshold" : "está pelo menos",
	"edu.cmu.cs.stage3.alice.core.question.IsCloseTo.object" : "de",
	"edu.cmu.cs.stage3.alice.core.question.IsFarFrom.object" : "a partir de",

	"edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Renderer" : "DirectX 7",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.openglrenderer.Renderer" : "OpenGL",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.java3drenderer.Renderer" : "Java3D",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.Renderer" : "JOGL",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer.Renderer" : "Nenhum",

	edu.cmu.cs.stage3.alice.core.styles.TraditionalAnimationStyle.BEGIN_AND_END_GENTLY : "INÍCIO_E_FIM_SUAVEMENTE",
	edu.cmu.cs.stage3.alice.core.styles.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY : "INÍCIO_SUAVEMENTE_E_FIM_BRUSCAMENTE",
	edu.cmu.cs.stage3.alice.core.styles.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY : "INÍCIO_BRUSCAMENTE_E_FIM_SUAVEMENTE",
	edu.cmu.cs.stage3.alice.core.styles.TraditionalAnimationStyle.BEGIN_AND_END_ABRUPTLY : "INÍCIO_E_FIM_BRUSCAMENTE",

	edu.cmu.cs.stage3.alice.core.Direction.LEFT : "ESQUERDA",
	edu.cmu.cs.stage3.alice.core.Direction.RIGHT : "DIREITA",
	edu.cmu.cs.stage3.alice.core.Direction.UP : "CIMA",
	edu.cmu.cs.stage3.alice.core.Direction.DOWN : "BAIXO",
	edu.cmu.cs.stage3.alice.core.Direction.FORWARD : "FRENTE",
	edu.cmu.cs.stage3.alice.core.Direction.BACKWARD : "ATRÁS",

	edu.cmu.cs.stage3.alice.core.SpatialRelation.LEFT_OF : "ESQUERDA_DE",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF : "DIREITA_DE",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.ABOVE : "ACIMA",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BELOW : "ABAIXO",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF : "EM_FRENTE_DE",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND : "ATRÁS",

	edu.cmu.cs.stage3.alice.core.Dimension.ALL : "TUDO",
	edu.cmu.cs.stage3.alice.core.Dimension.LEFT_TO_RIGHT : "ESQUERDA_PARA_DIREITA",
	edu.cmu.cs.stage3.alice.core.Dimension.TOP_TO_BOTTOM : "TOPO_PARA_BASE",
	edu.cmu.cs.stage3.alice.core.Dimension.FRONT_TO_BACK : "FRENTE_PARA_ATRÁS",

	edu.cmu.cs.stage3.alice.core.FogStyle.NONE : "NENHUM",
	edu.cmu.cs.stage3.alice.core.FogStyle.LINEAR : "LINEAR",
	edu.cmu.cs.stage3.alice.core.FogStyle.EXPONENTIAL : "EXPONENCIAL",

	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.SOLID : "SOLIDO",
 	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.WIREFRAME : "REPRESENTAÇÃOARAMADA",
	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.POINTS : "PONTOS",

	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.NONE : "NENHUM",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.FLAT : "PLANO",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.SMOOTH : "LISO",

	Boolean.TRUE : "verdadeiro",
	Boolean.FALSE : "falso",

	edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE : "BRANCO",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK : "PRETO",
	edu.cmu.cs.stage3.alice.scenegraph.Color.RED : "VERMELHO",
	edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN : "VERDE",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE : "AZUL",
	edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW : "AMARELO",
	edu.cmu.cs.stage3.alice.scenegraph.Color.PURPLE : "ROXO",
	edu.cmu.cs.stage3.alice.scenegraph.Color.ORANGE : "LARANJA",
	edu.cmu.cs.stage3.alice.scenegraph.Color.PINK : "ROSA",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BROWN : "MARROM",
	edu.cmu.cs.stage3.alice.scenegraph.Color.CYAN : "CIANO",
	edu.cmu.cs.stage3.alice.scenegraph.Color.MAGENTA : "MAGENTA",
	edu.cmu.cs.stage3.alice.scenegraph.Color.GRAY : "CINZA",
	edu.cmu.cs.stage3.alice.scenegraph.Color.LIGHT_GRAY : "CINZA_CLARO",
	edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY : "CINZA_ESCURO",

	edu.cmu.cs.stage3.util.HowMuch.INSTANCE : "INSTÂNCIA",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS : "INSTÂNCIA_E_PARTES",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS : "INSTÂNCIA_E_TODOS_DESCENTES",
}

htmlNameMap = {
	"edu.cmu.cs.stage3.alice.core.Transformable" : "Objeto",
	"edu.cmu.cs.stage3.alice.core.Model" : "Objeto",
	"java.lang.Number" : "Número",
	"java.lang.Boolean" : "Booleano",
	"java.lang.String" : "CadeiaDeCaracteres",
	"edu.cmu.cs.stage3.alice.scenegraph.Color" : "Cor",
	"edu.cmu.cs.stage3.alice.core.TextureMap" : "Textura",
	"edu.cmu.cs.stage3.alice.core.Sound" : "Som",
	"edu.cmu.cs.stage3.alice.core.Pose" : "Pose",
	"edu.cmu.cs.stage3.math.Vector3" : "Posição",
	"edu.cmu.cs.stage3.math.Quaternion" : "Orientação",
	"edu.cmu.cs.stage3.math.Matrix44" : "PontoDeVista",
	"edu.cmu.cs.stage3.alice.core.ReferenceFrame" : "Objeto",
	"edu.cmu.cs.stage3.alice.core.Light" : "Luz",
	"edu.cmu.cs.stage3.alice.core.Direction" : "Direção",
	"edu.cmu.cs.stage3.alice.core.Collection" : "[]",
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


#########################
# Misc
#########################

miscMap["javaLikeSyntax"] = "true"


####################################
# transfer resource data to Alice
####################################

resourceTransferFile = os.path.join( JAlice.getAliceHomeDirectoryString(), "resources/common/ResourceTransfer.py" )
execfile( resourceTransferFile)

