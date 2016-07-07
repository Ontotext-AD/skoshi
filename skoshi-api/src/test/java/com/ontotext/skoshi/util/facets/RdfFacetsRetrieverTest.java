package com.ontotext.skoshi.util.facets;

import com.ontotext.skoshi.model.entity.NamedEntity;
import com.ontotext.skoshi.model.navigation.TreeNode;
import com.ontotext.skoshi.rdf.SKOSX;
import com.ontotext.skoshi.tree.Tree;
import com.ontotext.skoshi.util.semanticstore.QueryExecutorUtils;
import com.ontotext.skoshi.util.semanticstore.RepositoryConnectionProvider;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RdfFacetsRetrieverTest {

    private static RepositoryConnection semanticStore;
    private static QueryExecutorUtils executorUtils;

    private URI idA = new URIImpl(":a"); Literal lA = new LiteralImpl("a");
    private URI idAA = new URIImpl(":aa"); Literal lAA = new LiteralImpl("aa");
    private URI idAB = new URIImpl(":ab"); Literal lAB = new LiteralImpl("ab");
    private URI idABA = new URIImpl(":aba"); Literal lABA = new LiteralImpl("aba");
    private URI idAC = new URIImpl(":ac"); Literal lAC = new LiteralImpl("ac");

    private URI idB = new URIImpl(":b"); Literal lB = new LiteralImpl("b");
    private URI idBA = new URIImpl(":ba"); Literal lBA = new LiteralImpl("ba");
    private URI idBB = new URIImpl(":bb"); Literal lBB = new LiteralImpl("bb");

    private URI idD = new URIImpl(":d"); Literal lD = new LiteralImpl("d");
    private URI idDA = new URIImpl(":da"); Literal lDA = new LiteralImpl("da");

    @BeforeClass
    public static void setUp() throws Exception {
        Repository repository = new SailRepository(
                                        new ForwardChainingRDFSInferencer(
                                        new MemoryStore()));
        repository.initialize();
        executorUtils = new QueryExecutorUtils(new RepositoryConnectionProvider(repository));
        semanticStore = repository.getConnection();
        semanticStore.add(RdfFacetsRetrieverTest.class.getClassLoader().getResourceAsStream("skos.rdf"), SKOS.NAMESPACE, RDFFormat.RDFXML);
    }

    @Before
    public void before() throws Exception {

//        a
//                aa
//                ab
//                        aba
//                ac
//        b
//                ba
//                bb
//                        aba
//        d
//                da

        semanticStore.add(idA, RDF.TYPE, SKOSX.FACET);
        semanticStore.add(idA, SKOS.PREF_LABEL, lA);

        semanticStore.add(idAA, RDF.TYPE, SKOS.CONCEPT);
        semanticStore.add(idAA, SKOS.PREF_LABEL, lAA);
        semanticStore.add(idA, SKOSX.HAS_FACET_CONCEPT, idAA);

        semanticStore.add(idAB, RDF.TYPE, SKOS.CONCEPT);
        semanticStore.add(idAB, SKOS.PREF_LABEL, lAB);
        semanticStore.add(idA, SKOSX.HAS_FACET_CONCEPT, idAB);

        semanticStore.add(idABA, RDF.TYPE, SKOS.CONCEPT);
        semanticStore.add(idABA, SKOS.PREF_LABEL, lABA);
        semanticStore.add(idA, SKOSX.HAS_FACET_CONCEPT, idABA);

        semanticStore.add(idAB, SKOS.NARROWER, idABA);
        semanticStore.add(idABA, SKOS.BROADER, idAB);

        semanticStore.add(idAC, RDF.TYPE, SKOS.CONCEPT);
        semanticStore.add(idAC, SKOS.PREF_LABEL, lAC);
        semanticStore.add(idA, SKOSX.HAS_FACET_CONCEPT, idAC);



        semanticStore.add(idB, RDF.TYPE, SKOSX.FACET);
        semanticStore.add(idB, SKOS.PREF_LABEL, lB);

        semanticStore.add(idBA, RDF.TYPE, SKOS.CONCEPT);
        semanticStore.add(idBA, SKOS.PREF_LABEL, lBA);
        semanticStore.add(idB, SKOSX.HAS_FACET_CONCEPT, idBA);

        semanticStore.add(idBB, RDF.TYPE, SKOS.CONCEPT);
        semanticStore.add(idBB, SKOS.PREF_LABEL, lBB);
        semanticStore.add(idB, SKOSX.HAS_FACET_CONCEPT, idBB);

        semanticStore.add(idB, SKOSX.HAS_FACET_CONCEPT, idABA);

        semanticStore.add(idBB, SKOS.NARROWER, idABA);
        semanticStore.add(idABA, SKOS.BROADER, idBB);

        semanticStore.add(idD, RDF.TYPE, SKOSX.FACET);
        semanticStore.add(idD, SKOS.PREF_LABEL, lD);

        semanticStore.add(idDA, RDF.TYPE, SKOS.CONCEPT);
        semanticStore.add(idDA, SKOS.PREF_LABEL, lDA);
        semanticStore.add(idD, SKOSX.HAS_FACET_CONCEPT, idDA);
    }

    @Test
    public void testCategories() throws Exception {
        FacetsRetriever facetsRetriever = new RdfFacetsRetriever(executorUtils);
        List<NamedEntity> categories = facetsRetriever.getFacetsCategories();

        assertThat(categories, hasSize(3));
        assertThat(categories.get(0).getId(), equalTo(idA));
        assertThat(categories.get(1).getId(), equalTo(idB));
        assertThat(categories.get(2).getId(), equalTo(idD));
    }

    @Test
    public void testGetFacets() throws Exception {
        FacetsRetriever facetsRetriever = new RdfFacetsRetriever(executorUtils);
        Map<URI, Tree<TreeNode>> facets = facetsRetriever.getFacets();

        assertThat(facets, notNullValue());
        assertThat(facets.size(), equalTo(3));

        Tree<TreeNode> tA = facets.get(idA);
        assertThat(tA, notNullValue());

        List<Tree<TreeNode>> lvlA1 = new ArrayList<>(tA.getSubTrees());

        assertThat(lvlA1, notNullValue());
        assertThat(lvlA1, hasSize(3));

        assertThat(lvlA1.get(0).getHead().getId(), equalTo(idAA));
        assertThat(lvlA1.get(1).getHead().getId(), equalTo(idAB));
        assertThat(lvlA1.get(2).getHead().getId(), equalTo(idAC));

        assertThat(lvlA1.get(1).getSubTrees().iterator().next().getHead().getId(), equalTo(idABA));


        Tree<TreeNode> tB = facets.get(idB);
        assertThat(tB, notNullValue());

        List<Tree<TreeNode>> lvlB1 = new ArrayList<>(tB.getSubTrees());

        assertThat(lvlB1, notNullValue());
        assertThat(lvlB1, hasSize(2));

        assertThat(lvlB1.get(0).getHead().getId(), equalTo(idBA));
        assertThat(lvlB1.get(1).getHead().getId(), equalTo(idBB));

        assertThat(lvlB1.get(1).getSubTrees().iterator().next().getHead().getId(), equalTo(idABA));

        Tree<TreeNode> tD = facets.get(idD);
        assertThat(tD, notNullValue());

        assertThat(tD.getSubTrees(), hasSize(1));

        Tree<TreeNode> tDA = tD.getSubTrees().iterator().next();

        assertThat(tDA.getHead().getId(), equalTo(idDA));
        assertThat(tDA.getSubTrees(), hasSize(0));

    }

}
