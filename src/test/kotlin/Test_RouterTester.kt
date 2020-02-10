import com.tinkerpop.blueprints.Graph
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.tg.TinkerGraph
import models.Answer
import models.AnswerType
import models.items.IDialogItem
import models.items.Router
import models.items.phrase.SimplePhrase
import models.items.text.SinglePhraseText
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tools.RouterTester
import java.lang.IllegalArgumentException
import kotlin.streams.toList

class Test_RouterTester {
    @Test
    fun isGraphRelated_good(){
        val graph: Graph = createRelatedTestGraph();
        val items = arrayListOf<IDialogItem>( SimplePhrase("id"))
        val router = Router("id", graph, items.toTypedArray())
        try{
            RouterTester.test(router).isGraphRelated()
        }catch (e: IllegalAccessException){
            assert(false)
        }
        assert(true)
    }

    @Test
     fun isGraphRelated_bad(){
        val graph: Graph = createNotRelatedTestGraph();
        val items = arrayListOf<IDialogItem>( SimplePhrase("id"))
        val router = Router("id", graph, items.toTypedArray())
        assertThrows<IllegalAccessException> {  RouterTester.test(router).isGraphRelated()}

    }

    @Test
     fun isAllItemsHasVertexes_good(){
        val graph: Graph = createNotRelatedTestGraph();
        val items = createListWithSimplePhrase(5).toTypedArray();
        val router = Router("id", graph, items);
        try{
            RouterTester.test(router).isAllItemsHasVertex()
        }catch (e: IllegalAccessException){
            assert(false)
        }
        assert(true);
    }

    @Test
    fun isAllItemsHasVertexes_bad(){
        val graph: Graph = createNotRelatedTestGraph();
        val items = createListWithSimplePhrase(20).toTypedArray();
        val router = Router("id", graph, items);

        assertThrows<IllegalAccessException> { RouterTester.test(router).isAllItemsHasVertex()}

    }

    @Test
     fun isAllVertexHasItems_good(){
        val graph: Graph = createNotRelatedTestGraph();
        val items = createListWithSimplePhrase(20).toTypedArray();
        val router = Router("id", graph, items);
        try{
            RouterTester.test(router).isAllVertexHasItems()
        }catch (e: IllegalAccessException){
            assert(false)
        }
        assert(true);
    }

    @Test
     fun isAllVertexHasItems_bad(){
        val graph: Graph = createNotRelatedTestGraph();
        val items = createListWithSimplePhrase(10).toTypedArray();
        val router = Router("id", graph, items);
        assertThrows<IllegalAccessException> {RouterTester.test(router).isAllVertexHasItems()}
    }

    @Test
    fun emptyVertexList(){
        val graph: Graph = TinkerGraph()
        val items = createListWithSimplePhrase(10).toTypedArray();
        val router = Router("id", graph, items);
        assertThrows<IllegalAccessException> {RouterTester.test(router).isAllVertexHasItems()}
    }

    @Test
     fun emptyItemsList(){
        val graph: Graph = createNotRelatedTestGraph();
        val items = emptyArray<IDialogItem>()
        val router = Router("id", graph, items);
        assertThrows<IllegalAccessException> {RouterTester.test(router).isAllVertexHasItems()}
    }

    @Test
    fun startPoint_good(){
        val graph: Graph = createNotRelatedTestGraph();
        val items = createListWithSimplePhrase(10).toTypedArray();
        val router = Router("id", graph, items);
        router.startPointId = "1";
        try{
            RouterTester.test(router).checkStartPoint()
        }catch (e: IllegalAccessException){
            assert(false)
        }
        assert(true);
    }

    @Test
    fun startPoint_bad(){
        val graph: Graph = createNotRelatedTestGraph();
        val items = createListWithSimplePhrase(10).toTypedArray();
        val router = Router("id", graph, items);
        router.startPointId = "112445";
        assertThrows<IllegalAccessException> {RouterTester.test(router).checkStartPoint()}
    }

    @Test
   fun startPointNull(){
        val graph: Graph = createNotRelatedTestGraph();
        val items = createListWithSimplePhrase(10).toTypedArray();
        val router = Router("id", graph, items);
        assertThrows<IllegalAccessException> {RouterTester.test(router).checkStartPoint()}
    }

    @Test
     fun startPointSetNull(){
        val graph: Graph = createNotRelatedTestGraph();
        val items = createListWithSimplePhrase(10).toTypedArray();
        val router = Router("id", graph, items);

        assertThrows<IllegalArgumentException> {
            router.startPointId = null;
            RouterTester.test(router).checkStartPoint()
        }
    }

    @Test
     fun isItemsLinkedRight_good(){
        val graph: Graph = createRelatedTestGraph();
        val items = createItemsListToRelatedGraph().toTypedArray();
        val router = Router("id", graph, items);
        try{
            RouterTester.test(router).isItemsLinkedCorrectly()
        }catch (e: IllegalAccessException){
            println(e)
            assert(false)
        }
        assert(true);
    }

    @Test
    fun isItemsLinkedRight_bad(){
        val graph: Graph = createNotRelatedTestGraph();
        val items = createItemsListToRelatedGraph().toTypedArray();
        val router = Router("id", graph, items);
        assertThrows<IllegalAccessException> {  RouterTester.test(router).isItemsLinkedCorrectly()}
    }

    @Test
    fun isAllTypeCorrect_good(){
        val graph: Graph = createRelatedTestGraph();
        val items = createItemsListToRelatedGraph().toTypedArray();
        val router = Router("id", graph, items);
       try{
           RouterTester.test(router).checkTypesOfPhases()
       }catch (e: Exception){
           println(e)
           assert(false);
       }
        assert(true)
    }

    @Test
    fun isAllTypeCorrect_bad(){
        val graph: Graph = createRelatedTestGraph();
        val items = createItemsListToRelatedGraph().toTypedArray();
        items[6].getAnswers()[0].type = AnswerType.EXIT;
        val router = Router("id", graph, items);
        assertThrows<IllegalAccessException> {  RouterTester.test(router).checkTypesOfPhases()}
    }


    private fun createListWithSimplePhrase(maxId: Int): List<IDialogItem>{
        val list = arrayListOf<IDialogItem>();
        for( i in 1..maxId){
            list.add(SimplePhrase(i.toString()))
        }
        return list;
    }


    private fun createItemsListToRelatedGraph(): List<IDialogItem>{
        val v = hashMapOf<Int, IDialogItem>();
        val a = hashMapOf<Int, SinglePhraseText>();

        a[1] = SinglePhraseText("1", "text 1 ", arrayOf(Answer("2", "answ 1")))
        a[2] = SinglePhraseText("2", "text 2 ", arrayOf(Answer("3", "answ 2.1"), Answer("4","answ 2.2" )))
        a[3] = SinglePhraseText("3", "text 3 ", arrayOf(Answer("5", "answ 3.1"), Answer("6", "answ 6.1")))
        a[4] = SinglePhraseText("4", "text 4 ", arrayOf(Answer("6", "answ 4.1"), Answer("7", "answ 4.2") ))
        a[5] = SinglePhraseText("5", "text 5 ", arrayOf(Answer("9", "answ 5")))
        a[6] = SinglePhraseText("6", "text 6 ", arrayOf(Answer("9", "answ 6")))
        a[7] = SinglePhraseText("7", "text 7 ", arrayOf(Answer("8", "answ 1")))
        a[8] = SinglePhraseText("8", "text 8 ", arrayOf(Answer("end 8", "answ 8", AnswerType.EXIT)))
        a[9] = SinglePhraseText("9", "text 9 ", arrayOf(Answer("10", "answ 9")))
        a[10] = SinglePhraseText("10", "text 10 ", arrayOf(Answer("edn 10", "answ 1", AnswerType.EXIT)))
        a[11] = SinglePhraseText("11", "text 11 ", arrayOf(Answer("2", "answ 11")))

        v[1] = SimplePhrase(1.toString());

        return a.values.stream().map { SimplePhrase(it.getId(), it) }.toList()

        /*

        graph.addEdge(null, v[11], v[2],"11->2")
        graph.addEdge(null, v[1], v[2],"1->2")
        graph.addEdge(null, v[2], v[3],"2->3")
        graph.addEdge(null, v[2], v[4],"2->4")
        graph.addEdge(null, v[3], v[5],"3->5")
        graph.addEdge(null, v[3], v[6],"3->6")
        graph.addEdge(null, v[4], v[7],"4->7")
        graph.addEdge(null, v[7], v[8],"7->8")
        graph.addEdge(null, v[9], v[10],"9->10")
         */
    }


    private fun createNotRelatedTestGraph(): Graph{
        val graph: Graph = TinkerGraph();
        val v = hashMapOf<Int, Vertex>()
        for (i in 1 .. 11) {
            v[i] =  graph.addVertex(i.toString())
        }

        graph.addEdge(null, v[11], v[2],"11->2")
        graph.addEdge(null, v[1], v[2],"1->2")
        graph.addEdge(null, v[2], v[3],"2->3")
        graph.addEdge(null, v[2], v[4],"2->4")
        graph.addEdge(null, v[3], v[5],"3->5")
        graph.addEdge(null, v[3], v[6],"3->6")
        graph.addEdge(null, v[4], v[7],"4->7")
        graph.addEdge(null, v[4], v[7],"4->6")
        graph.addEdge(null, v[7], v[8],"7->8")
        graph.addEdge(null, v[9], v[10],"9->10")

        return graph;
    }

    private fun createRelatedTestGraph(): Graph{
        val graph: Graph = TinkerGraph();
        val v = hashMapOf<Int, Vertex>()
        for (i in 1 .. 11) {
            v[i] =  graph.addVertex(i.toString())
        }

        graph.addEdge(null, v[11], v[2],"11->2")
        graph.addEdge(null, v[1], v[2],"1->2")
        graph.addEdge(null, v[2], v[3],"2->3")
        graph.addEdge(null, v[2], v[4],"2->4")
        graph.addEdge(null, v[3], v[5],"3->5")
        graph.addEdge(null, v[3], v[6],"3->6")
        graph.addEdge(null, v[4], v[7],"4->7")
        graph.addEdge(null, v[4], v[6],"4->6")
        graph.addEdge(null, v[7], v[8],"7->8")
        graph.addEdge(null, v[5], v[9],"5->9")
        graph.addEdge(null, v[6], v[9],"6->9")
        graph.addEdge(null, v[9], v[10],"9->10")

        return graph;
    }
}