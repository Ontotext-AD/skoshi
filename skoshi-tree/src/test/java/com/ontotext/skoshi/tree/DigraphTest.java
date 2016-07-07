package com.ontotext.skoshi.tree;

import org.junit.Test;

import java.util.Comparator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class DigraphTest {

    @Test
    public void testBuildGraph() {

//        a
//            b
//                c
//        d
//            e
//        f
//            g

        Digraph<Character> digraph = new DigraphImpl<>();

        digraph.addEdge('a', 'b');
        digraph.addEdge('b', 'c');
        digraph.addEdge('d', 'e');
        digraph.addEdge('f', 'g');

        System.out.println("ROOTS: " + digraph.findRoots());
        System.out.println(digraph);

        assertThat(digraph.findRoots(), hasItems('a', 'd', 'f'));

        assertThat(digraph.adj('a'), hasSize(1));
        assertThat(digraph.adj('a').get(0), equalTo('b'));

        assertThat(digraph.adj('b'), hasSize(1));
        assertThat(digraph.adj('b').get(0), equalTo('c'));

        assertThat(digraph.adj('c'), hasSize(0));

        assertThat(digraph.adj('d'), hasSize(1));
        assertThat(digraph.adj('d').get(0), equalTo('e'));

        assertThat(digraph.adj('e'), hasSize(0));

        assertThat(digraph.adj('f'), hasSize(1));
        assertThat(digraph.adj('f').get(0), equalTo('g'));

        assertThat(digraph.adj('g'), hasSize(0));

        digraph.sort(new Comparator<Character>() {
            public int compare(Character a, Character b) {
                if (a.compareTo(b) < 0)
                    return -1;
                else
                if (a.compareTo(b) > 0)
                    return 1;
                else return 0;
            }
        });

        assertThat(digraph.findRoots(),  contains('a', 'd', 'f'));
    }

}
