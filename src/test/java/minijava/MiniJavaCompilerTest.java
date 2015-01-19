package minijava;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import minijava.backend.i386.I386MachineSpecifics;

import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class MiniJavaCompilerTest {

	@Parameterized.Parameters
	public static Collection<Object[]> files() throws IOException {
		return TestFiles.getFiles();
	}

	private File file;
	private Class<? extends Exception> exceptionClass;
	private MiniJavaCompiler compiler;
	private static Map<String, String> outputs;
	
	@BeforeClass
	public static void setUpOutputs() {
		outputs = new HashMap<>();
		outputs.put("ArrayAccess", "5\n");
		outputs.put("ArrSum", "55\n");
		outputs.put("BinarySearch", "20\n21\n22\n23\n24\n25\n26\n27\n28\n29\n30\n31\n32\n33\n34\n35\n36\n37\n38\n99999\n0\n0\n1\n1\n1\n1\n0\n0\n999\n");
		outputs.put("BinaryTree", "16\n100000000\n8\n16\n4\n8\n12\n14\n16\n20\n24\n28\n1\n1\n1\n0\n1\n4\n8\n14\n16\n20\n24\n28\n0\n0\n");
		outputs.put("BubbleSort", "20\n7\n12\n18\n2\n11\n6\n9\n19\n5\n99999\n2\n5\n6\n7\n9\n11\n12\n18\n19\n20\n0\n");
		outputs.put("E", "27182818284590452353602874713526624977572470936999595749669676277240766303535475945713821785251664274274663919320030599218174135966290435729003342952605956307381323286279434907632338298807531952510190115738341879307021540891499348841675092447614606680822648001684774118537423454424371075390777449920695517027618386062613313845830007520449338265602976067371132007093287091274437470472306969772093101416928368190255151086574637721112523897844250569536967707854499699679468644549059879316368892300987931277361782154249992295763514822082698951936680331825288693984964651058209392398294887933203625094431173012381970684161403970198376793206832823764648042953118023287825098194558153017567173613320698112509961818815930416903515988885193458072738667385894228792284998920868058257492796104841984443634632449684875602336248270419786232090021609902353043699418491463140934317381436405462531520961836908887070167683964243781405927145635490613031072085103837505101157477041718986106873969655212671546889570350354021234078498193343210681701210056278802351930332247450158539047304199577770935036604169973297250886876966403555707162268447162560798826517871341951246652010305921236677194325278675398558944896970964097545918569563802363701621120477427228364896134225164450781824423529486363721417402388934412479635743702637552944483379980161254922785092577825620926226483262779333865664816277251640191059004916449982893150566047258027786318641551956532442586982946959308019152987211725563475463964479101459040905862984967912874068705048958586717479854667757573205681288459205413340539220001137863009455606881667400169842055804033637953764520304024322566135278369511778838638744396625322498506549958862342818997077332761717839280349465014345588970719425863987727547109629537415211151368350627526023264847287039207643100595841166120545297030236472549296669381151373227536450988890313602057248176585118063036442812314965507047510254465011727211555194866850800368532281831521960037356252794495158284188294787610852639813955990067376482922443752871846245780361929819713991475644882626039033814418232625150974827987779964373089970388867782271383605772978824125611907176639465070633045279546618550966661856647097113444740160704626215680717481877844371436988218559670959102596862002353718588748569652200050311734392073211390803293634479727355955277349071783793421637012050054513263835440001863239914907054797780566978533580489669062951194324730995876552368128590413832411607226029983305353708761389396391779574540161372236187893652605381558415871869255386061647798340254351284396129460352913325942794904337299085731580290958631382683291477116396337092400316894586360606458459251269946557248391865642097526850823075442545993769170419777800853627309417101634349076964237222943523661255725088147792231519747780605696725380171807763603462459278778465850656050780844211529697521890874019660906651803516501792504619501366585436632712549639908549144200014574760819302212066024330096412704894390397177195180699086998606636583232278709376502260149291011517177635944602023249300280401867723910288097866605651183260043688508817157238669842242201024950551881694803221002515426494639812873677658927688163598312477886520141174110913601164995076629077943646005851941998560162647907615321038727557126992518275687989302761761146162549356495903798045838182323368612016243736569846703785853305275833337939907521660692380533698879565137285593883499894707416181550125397064648171946708348197214488898790676503795903669672494992545279033729636162658976039498576741397359441023744329709355477982629614591442936451428617158587339746791897571211956187385783644758448423555581050025611492391518893099463428413936080383091662818811503715284967059741625628236092168075150177725387402564253470879089137291722828611515915683725241630772254406337875931059826760944203261924285317018781772960235413060672136046000389661093647095141417185777014180606443636815464440053316087783143174440811949422975599314011888683314832802706553833004693290115744147563139997221703804617092894579096271662260740718749975359212756084414737823303270330168237193648002173285734935947564334129943024850235732214597843282641421684878721673367010615094243456984401873312810107945127223737886126058165668053714396127888732527373890392890506865324138062796025930387727697783792868409325365880733988457218746021005311483351323850047827169376218004904795597959290591655470505777514308175112698985188408718564026035305583737832422924185625644255022672155980274012617971928047139600689163828665277009752767069777036439260224372841840883251848770472638440379530166905465937461619323840363893131364327137688841026811219891275223056256756254701725086349765367288605966752740868627407912856576996313789753034660616669804218267724560530660773899624218340859882071864682623215080288286359746839654358856685503773131296587975810501214916207656769950659715344763470320853215603674828608378656803073062657633469774295634643716709397193060876963495328846833613038829431040800296873869117066666146800015121143442256023874474325250769387077775193299942137277211258843608715834835626961661980572526612206797540621062080649882918454395301529982092503005498257043390553570168653120526495614857249257386206917403695213533732531666345466588597286659451136441370331393672118569553952108458407244323835586063106806964924851232632699514603596037297253198368423363904632136710116192821711150282801604488058802382031981493096369596735832742024988245684941273860566491352526706046234450549227581151709314921879592718001940968866986837037302200475314338181092708030017205935530520700706072233999463990571311587099635777359027196285061146514837526209565346713290025994397663114545902685898979115837093419370441155121920117164880566945938131183843765620627846310490346293950029458341164824114969758326011800731699437393506966295712410273239138741754923071862454543222039552735295240245903805744502892246886285336542213815722131163288112052146489805180092024719391710555390113943316681515828843687606961102505171007392762385553386272553538830960671644662370922646809671254061869502143176211668140097595281493907222601112681153108387317617323235263605838173151034595736538223534992935822836851007810884634349983518404451704270189381994243410090575376257767571118090088164183319201962623416288166521374717325477727783488774366518828752156685719506371936565390389449366421764003121527870222366463635755503565576948886549500270853923617105502131147413744106134445544192101336172996285694899193369184729478580729156088510396781959429833186480756083679551496636448965592948187851784038773326247051945050419847742014183947731202815886845707290544057510601285258056594703046836344592652552137008068752009593453607316226118728173928074623094685367823106097921599360019946237993434210687813497346959246469752506246958616909178573976595199392993995567542714654910456860702099012606818704984178079173924071945996323060254707901774527513186809982284730860766536866855516467702911336827563107223346726113705490795365834538637196235856312618387156774118738527722922594743373785695538456246801013905727871016512966636764451872465653730402443684140814488732957847348490003019477888020460324660842875351848364959195082888323206522128104190448047247949291342284951970022601310430062410717971502793433263407995960531446053230488528972917659876016667811937932372453857209607582277178483361613582612896226118129455927462767137794487586753657544861407611931125958512655759734573015333642630767985443385761715333462325270572005303988289499034259566232975782488735029259166825894456894655992658454762694528780516501720674785417887982276806536650641910973434528878338621726156269582654478205672987756426325321594294418039943217000090542650763095588465895171709147607437136893319469090981904501290307099566226620303182649365733698419555776963787624918852865686607600566025605445711337286840205574416030837052312242587223438854123179481388550075689381124935386318635287083799845692619981794523364087429591180747453419551420351726184200845509170845682368200897739455842679214273477560879644279202708312150156406341341617166448069815483764491573900121217041547872591998943825364950514771379399147205219529079396137621107238494290616357604596231253506068537651423115349665683715116604220796394466621163255157729070978473156278277598788136491951257483328793771571459091064841642678309949723674420175862269402159407924480541255360431317992696739157542419296607312393763542139230617876753958711436104089409966089471418340698362993675362621545247298464213752891079884381306095552622720837518629837066787224430195793793786072107254277289071732854874374355781966511716618330881129120245204048682200072344035025448202834254187884653602591506445271657700044521097735585897622655484941621714989532383421600114062950718490427789258552743035221396835679018076406042138307308774460170842688272261177180842664333651780002171903449234264266292261456004337383868335555343453004264818473989215627086095650629340405264943244261445665921291225648893569655009154300\n");
		outputs.put("Effects", "0\n");
		outputs.put("Factorial", "3628800\n");
		outputs.put("Fib", "1973\n");
		outputs.put("FibL", "1973\n");
		outputs.put("GameOfLife", "64996\n71097\n76875\n87498\n88137\n84740\n96705\n91420\n116099\n79512\n91456\n93370\n105344\n103035\n110942\n80700\n84818\n89765\n102305\n101800\n108620\n107338\n96301\n115194\n84415\n98656\n93209\n94275\n98564\n99724\n84909\n91366\n97886\n109247\n109898\n106889\n119558\n115031\n139722\n103503\n116233\n118917\n130913\n129024\n137661\n108187\n112311\n117644\n130958\n131209\n138047\n137161\n126874\n146511\n115756\n130375\n125698\n127520\n131819\n133373\n119324\n126553\n133085\n144860\n146267\n144024\n156731\n152582\n169934\n126364\n146463\n149147\n161143\n159254\n167891\n138417\n142541\n147874\n161188\n161439\n168277\n167391\n157104\n176741\n145986\n160605\n155928\n157750\n176361\n142310\n106576\n91366\n97886\n109247\n109898\n106889\n119558\n115031\n139722\n103503\n116233\n118917\n130913\n129024\n137661\n108187\n112311\n117644\n130958\n131209\n138047\n137161\n126874\n146511\n115756\n130375\n125698\n127520\n131819\n133373\n119324\n126553\n133085\n144860\n146267\n144024\n156731\n152582\n169934\n126364\n146463\n149147\n161143\n159254\n167891\n138417\n142541\n147874\n161188\n161439\n168277\n167391\n157104\n176741\n145986\n160605\n155928\n157750\n176361\n142310\n106576\n91366\n97886\n109247\n109898\n106889\n119558\n115031\n139722\n103503\n116233\n118917\n130913\n129024\n137661\n108187\n112311\n117644\n130958\n131209\n138047\n137161\n126874\n146511\n115756\n130375\n125698\n127520\n131819\n133373\n119324\n126553\n133085\n144860\n146267\n144024\n156731\n152582\n169934\n126364\n146463\n149147\n161143\n159254\n167891\n138417\n142541\n147874\n161188\n161439\n999999999\n");
		outputs.put("Graph", "1\n4\n999\n4\n1\n-999\n3\n2\n999\n4\n1\n-999\n3\n2\n999\n4\n3\n-999\n5\n5\n999\n5\n4\n-999\n0\n");
		outputs.put("LinearSearch", "10\n11\n12\n13\n14\n15\n16\n17\n18\n9999\n0\n1\n1\n0\n55\n");
		outputs.put("LinkedList", "25\n10000000\n39\n25\n10000000\n22\n39\n25\n1\n0\n10000000\n28\n22\n39\n25\n2220000\n-555\n-555\n28\n22\n25\n33300000\n22\n25\n44440000\n0\n");
		outputs.put("ManyArgs", "1\n0\n2\n1\n3\n1\n4\n0\n5\n1\n10\n0\n89\n1\n999\n");
		outputs.put("Newton", "2\n999\n577\n408\n0\n");
		outputs.put("Precedence", "5\n");
		outputs.put("Primes", "2\n3\n5\n7\n11\n13\n17\n19\n999\n8\n");
		outputs.put("QuickSort", "20\n7\n12\n18\n2\n11\n6\n9\n19\n5\n9999\n2\n5\n6\n7\n9\n11\n12\n18\n19\n20\n0\n");
		outputs.put("Scope", "5\n");
		outputs.put("Scope2", "5\n");
		outputs.put("ShortCutAnd", "0\n");
		outputs.put("Stck", "55\n");
		outputs.put("Sum", "15\n");
		outputs.put("SwitchArrayElements", "88\n");
		outputs.put("TestEq", "1\n0\n");
		outputs.put("TrivialClass", "555\n");
		outputs.put("While", "1\n3\n6\n10\n15\n21\n28\n36\n45\n55\n0\n");
	}
	
	public MiniJavaCompilerTest(File file, Class<? extends Exception> exceptionClass) {
		this.file = file;
		this.exceptionClass = exceptionClass;
	}
	
	@Before
	public void setUp() {
		compiler = new MiniJavaCompiler(new I386MachineSpecifics());
	}
	
	@Test
	public void testCompileExamples() throws IOException {

		System.out.println("Testing compiler input from file \"" + file.toString() + "\"");
		
		Configuration config =  new Configuration(new String[]{file.toString()});

		try {
			compiler.compile(config);
			String output = compiler.runExecutable(config, 15);
			if (exceptionClass != null) {
				fail("The example " + file.toString() + " should have failed with exception " + exceptionClass + ", but was accepted by the compiler.");
			}
			String expectedOutput = outputs.get(FilenameUtils.getBaseName(file.toString()));
			if (!output.equals(expectedOutput)) {
				fail("The example " + file.toString() + " should have printed '" + expectedOutput + "' but printed '" + output + "'");
			}
		}
		catch (Exception e) {
			
			if (exceptionClass == null) {
				fail("The example " + file.toString() + " should have been accepted by the compiler but failed: " + e.getMessage());
			}
			
			if (!exceptionClass.isInstance(e)) {
				fail("The example " + file.toString() + " should have failed with exception " + exceptionClass + " but with failed with exception " + e.getClass() + ", " + e.getMessage());
			}
		}
	}
}
