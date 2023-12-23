package com.tourist_bot.quad;


import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuadTreeTest {

    @Test
    public void testCreateTree() {
        double startX = 100;
        double endX = 200;
        double startY = 200;
        double quadrantLenX = 10;
        double quadrantLenY = 20;

        QuadTree<Object> tree = QuadTree.buildByMinMax(quadrantLenX, quadrantLenY, startX, endX, startY);

        assertEquals(520, tree.yMax);
        assertEquals(4, tree.quadDepth);
    }


    @Test
    public void testSearchInSiblingQuads() {
        int depth = 4;
        int quadrantLenX = 1;
        int quadrantLenY = 4;
        int quads = (int) Math.pow(2, depth);

        QuadTree<String> tree = QuadTree.buildByDepth(
                quadrantLenX,
                quadrantLenY,
                depth,
                0,
                0);


        TreeSet<QuadPoint<String>>[][] matrix = new TreeSet[quads][quads];
        {
            for (int x = 0; x < quads; x++) {
                for (int y = 0; y < quads; y++) {
                    matrix[x][y] = new TreeSet<>((o1, o2) -> {
                        int compareX = Double.compare(o1.x, o2.x);
                        if (compareX == 0) {
                            return Double.compare(o1.y, o2.y);
                        } else {
                            return compareX;
                        }
                    });
                }
            }

            //[[x:0.0,y:0.0,v:0],
            // [x:0.0,y:1.0,v:1],
            // [x:0.0,y:2.0,v:2],
            // [x:0.0,y:3.0,v:3],
            // [x:0.0,y:4.0,v:4],
            // [x:0.0,y:5.0,v:5],
            // [x:0.0,y:6.0,v:6],
            // [x:0.0,y:7.0,v:7],
            // [x:0.0,y:8.0,v:8],
            // [x:0.0,y:9.0,v:9],
            // [x:0.0,y:10.0,v:10],
            // [x:0.0,y:11.0,v:11],
            // [x:0.0,y:12.0,v:1 ...
            int cnt = 0;
            for (int x = 0; x < quads * quadrantLenX; x++) {
                for (int y = 0; y < quads * quadrantLenY; y++) {
                    int xQuad = (x / quadrantLenX);
                    int yQuad = (y / quadrantLenY);
                    double x1 = x + (0.5);
                    double y1 = y + (0.5);
                    String value = cnt + " xQ:" + xQuad + " yQuad:" + yQuad;
                    matrix[yQuad][xQuad].add(new QuadPoint<>(x1, y1, value));
                    tree.add(x1, y1, value);
                    cnt++;
                }
            }
        }

        //  test
        {


            for (int quad = 0; quad < quads; quad++) {
//                System.out.println();
//                System.out.println("Quad: " + quad);
                for (int x = 0; x < quads * quadrantLenX; x++) {
                    for (int y = 0; y < quads * quadrantLenY; y++) {
                        int xQuad = (x / quadrantLenX);
                        int yQuad = (y / quadrantLenY);

                        SortedSet<QuadPoint<String>> setExp = new TreeSet<>((o1, o2) -> {
                            int compareX = Double.compare(o1.x, o2.x);
                            if (compareX == 0) {
                                return Double.compare(o1.y, o2.y);
                            } else {
                                return compareX;
                            }
                        });
                        for (int i = xQuad - quad; i <= xQuad + quad; i++) {
                            for (int j = yQuad - quad; j <= yQuad + quad; j++) {
                                if (i >= 0 && j >= 0 && i < quads && j < quads) {
                                    SortedSet<QuadPoint<String>> set = matrix[j][i];
                                    setExp.addAll(set);
                                }
                            }
                        }


//                        System.out.println("xQuad: " + xQuad + " yQuad: " + yQuad);
                        double x1 = x + (0.5);
                        double y1 = y + (0.5);
//                        System.out.println("x: " + x1 + " y: " + y1);
                        Iterator<QuadPoint<String>> iter = tree.searchInSiblingQuads(x1, y1, quad);
                        SortedSet<QuadPoint<String>> setAct = new TreeSet<>((o1, o2) -> {
                            int compareX = Double.compare(o1.x, o2.x);
                            if (compareX == 0) {
                                return Double.compare(o1.y, o2.y);
                            } else {
                                return compareX;
                            }
                        });
                        while (iter.hasNext()) {
                            setAct.add(iter.next());
                        }

                        try {
                            assertEquals(setExp, setAct);
                        } catch (Throwable e) {
                            System.out.println(setExp.stream().map(QuadPoint::toString).collect(Collectors.joining(" ")));
                            System.out.println(setAct.stream().map(QuadPoint::toString).collect(Collectors.joining(" ")));
                            throw e;
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testSearchQuadrantsByRadius() {
        int depth = 4;
        double quadrantLenX = 4;
        double quadrantLenY = 4;

        QuadTree<Integer> tree = QuadTree.buildByDepth(
                quadrantLenX,
                quadrantLenY,
                depth,
                0,
                0);
        System.out.println(tree.printTree());
        int c = 0;
        for (int x = 1; x < tree.xMax; x += 2) {
            for (int y = 1; y < tree.yMax; y += 2) {
                if (x % quadrantLenY != 0 && y % quadrantLenY != 0) {
                    tree.add(x, y, c++);
                }
            }
        }

        {
            List<QuadTree.QuadLeaf<Integer>> quadLeaves = tree.searchQuadrantsByRadius(63, 63, 0);
            assertEquals(4, quadLeaves.stream().mapToInt(e -> e.data.size()).sum());
        }

        {
            List<QuadTree.QuadLeaf<Integer>> quadLeaves = tree.searchQuadrantsByRadius(66, 66, 1);
            assertEquals(0, quadLeaves.stream().mapToInt(e -> e.data.size()).sum());
        }
        {
            List<QuadTree.QuadLeaf<Integer>> quadLeaves = tree.searchQuadrantsByRadius(65, 65, 1);
            assertEquals(4, quadLeaves.stream().mapToInt(e -> e.data.size()).sum());
        }
        {
            List<QuadTree.QuadLeaf<Integer>> quadLeaves = tree.searchQuadrantsByRadius(65, 65, 4);
            assertEquals(4, quadLeaves.stream().mapToInt(e -> e.data.size()).sum());
        }
        {
            List<QuadTree.QuadLeaf<Integer>> quadLeaves = tree.searchQuadrantsByRadius(65, 65, 5);
            assertEquals(16, quadLeaves.stream().mapToInt(e -> e.data.size()).sum());
        }

    }

    @Test
    public void testSearchByRadius1() {
        int depth = 4;
        double quadrantLenX = 4;
        double quadrantLenY = 4;

        QuadTree<Integer> tree = QuadTree.buildByDepth(
                quadrantLenX,
                quadrantLenY,
                depth,
                0,
                0);
        System.out.println(tree.printTree());
        int c = 0;
        for (int x = 1; x < tree.xMax; x += 2) {
            for (int y = 1; y < tree.yMax; y += 2) {
                if (x % quadrantLenY != 0 && y % quadrantLenY != 0) {
                    tree.add(x, y, c++);
//                    System.out.println((x) + ":" + (y));
                }
            }
        }
//        tree.add(1.5, 1.5, 0);
        System.out.println();


        {
            Iterator<QuadPoint<Integer>> points = tree.searchByRadius(63, 63, 0);
            List<QuadPoint<Integer>> actualList = new ArrayList<>();
            points.forEachRemaining(actualList::add);
            assertEquals(1, actualList.size());
        }


        {
            Iterator<QuadPoint<Integer>> points = tree.searchByRadius(66, 66, 1);
            List<QuadPoint<Integer>> actualList = new ArrayList<>();
            points.forEachRemaining(actualList::add);
            assertEquals(0, actualList.size());
        }
        {
            Iterator<QuadPoint<Integer>> points = tree.searchByRadius(65, 65, 1);
            List<QuadPoint<Integer>> actualList = new ArrayList<>();
            points.forEachRemaining(actualList::add);
            assertEquals(0, actualList.size());
        }
        {
            Iterator<QuadPoint<Integer>> points = tree.searchByRadius(65, 65, 2);
            List<QuadPoint<Integer>> actualList = new ArrayList<>();
            points.forEachRemaining(actualList::add);
            assertEquals(0, actualList.size());
        }
        {
            Iterator<QuadPoint<Integer>> points = tree.searchByRadius(65, 65, 3);
            List<QuadPoint<Integer>> actualList = new ArrayList<>();
            points.forEachRemaining(actualList::add);
            assertEquals(1, actualList.size());
        }
        {
            Iterator<QuadPoint<Integer>> points = tree.searchByRadius(65, 65, 5);
            List<QuadPoint<Integer>> actualList = new ArrayList<>();
            points.forEachRemaining(actualList::add);
            assertEquals(3, actualList.size());
        }
        {
            Iterator<QuadPoint<Integer>> points = tree.searchByRadius(65, 65, 8);
            List<QuadPoint<Integer>> actualList = new ArrayList<>();
            points.forEachRemaining(actualList::add);
            assertEquals(8, actualList.size());
        }
        {
            Iterator<QuadPoint<Integer>> points = tree.searchByRadius(65, 65, 100);
            List<QuadPoint<Integer>> actualList = new ArrayList<>();
            points.forEachRemaining(actualList::add);
            assertEquals(1024, actualList.size());
        }

    }


    @Test
    public void testSearchByRadius2() {
        int depth = 4;
        double quadrantLenX = 3;
        double quadrantLenY = 3;

        QuadTree<String> tree = QuadTree.buildByDepth(
                quadrantLenX,
                quadrantLenY,
                depth,
                0,
                0);
        System.out.println(tree.printTree());


        SortedSet<QuadPoint<String>> points = new TreeSet<>((o1, o2) -> {
            int compareX = Double.compare(o1.x, o2.x);
            if (compareX == 0) {
                return Double.compare(o1.y, o2.y);
            } else {
                return compareX;
            }
        });
        {
            int c = 0;
            for (int x = 1; x < tree.xMax; x += 2) {
                for (int y = 1; y < tree.yMax; y += 2) {
                    if (x % quadrantLenY != 0 && y % quadrantLenY != 0) {
                        tree.add(x, y, c + "");
                        points.add(new QuadPoint<>(x, y, c + ""));
                    }
                }
            }
        }

        //  test
        {
            for (int radius = 0; radius < tree.yMax; radius += 2) {
                System.out.println(radius);
                for (int x = 0; x < tree.xMax; x++) {
                    for (int y = 0; y < tree.yMax; y++) {
                        SortedSet<QuadPoint<String>> setExp = new TreeSet<>((o1, o2) -> {
                            int compareX = Double.compare(o1.x, o2.x);
                            if (compareX == 0) {
                                return Double.compare(o1.y, o2.y);
                            } else {
                                return compareX;
                            }
                        });

                        for (QuadPoint<String> point : points) {
                            double dist = Math.sqrt(Math.pow(point.x - x, 2) + Math.pow(point.y - y, 2));
                            if (dist <= radius) setExp.add(point);
                        }

                        Iterator<QuadPoint<String>> iter = tree.searchByRadius(x, y, radius);
                        SortedSet<QuadPoint<String>> setAct = new TreeSet<>((o1, o2) -> {
                            int compareX = Double.compare(o1.x, o2.x);
                            if (compareX == 0) {
                                return Double.compare(o1.y, o2.y);
                            } else {
                                return compareX;
                            }
                        });
                        while (iter.hasNext()) {
                            setAct.add(iter.next());
                        }
                        try {
                            assertEquals(setExp, setAct);
                        } catch (Throwable e) {
                            System.out.println(setExp.stream().map(QuadPoint::toString).collect(Collectors.joining(" ")));
                            System.out.println(setAct.stream().map(QuadPoint::toString).collect(Collectors.joining(" ")));
                            throw e;
                        }



                    }
                }


            }
        }

//        int ddd = 0;
//
//        for (int radius = 0; radius < tree.yMax; radius += 2) {
////                System.out.println();
////                System.out.println("Quad: " + quad);
//            for (int x = 0; x < tree.xMax; x++) {
//                for (int y = 0; y < tree.yMax; y++) {
//                    int xQuad = (int) (x / quadrantLenX);
//                    int yQuad = (int) (y / quadrantLenY);
//
//                    SortedSet<Point<String>> setExp = new TreeSet<>((o1, o2) -> {
//                        int compareX = Double.compare(o1.x, o2.x);
//                        if (compareX == 0) {
//                            return Double.compare(o1.y, o2.y);
//                        } else {
//                            return compareX;
//                        }
//                    });
//                    for (int i = xQuad - radius; i <= xQuad + radius; i++) {
//                        for (int j = yQuad - radius; j <= yQuad + radius; j++) {
//                            if (i >= 0 && j >= 0 && i < radius && j < radius) {
//                                SortedSet<Point<String>> set = matrix[j][i];
//                                setExp.addAll(set);
//                            }
//                        }
//                    }
//
//
////                        System.out.println("xQuad: " + xQuad + " yQuad: " + yQuad);
//                    double x1 = x + (0.5);
//                    double y1 = y + (0.5);
////                        System.out.println("x: " + x1 + " y: " + y1);
//                    Iterator<Point<String>> iter = tree.searchByRadius(x1, y1, radius);
//                    SortedSet<Point<String>> setAct = new TreeSet<>((o1, o2) -> {
//                        int compareX = Double.compare(o1.x, o2.x);
//                        if (compareX == 0) {
//                            return Double.compare(o1.y, o2.y);
//                        } else {
//                            return compareX;
//                        }
//                    });
//                    while (iter.hasNext()) {
//                        setAct.add(iter.next());
//                    }
//
//                    try {
//                        assertEquals(setExp, setAct);
//                    } catch (Throwable e) {
//                        System.out.println(setExp.stream().map(Point::toString).collect(Collectors.joining(" ")));
//                        System.out.println(setAct.stream().map(Point::toString).collect(Collectors.joining(" ")));
//                        throw e;
//                    }
//                    ddd++;
////                        System.out.println();
//                }
//            }
//        }
    }


//        tree.add(7, 7, 7 + "-" + 7);
//        Iterator<Point<String>> iter = tree.searchInSiblingQuads(0, 0, 0);
//        SortedSet<Point<String>> setExp = new TreeSet<>((o1, o2) -> {
//            int compareX = Double.compare(o1.x, o2.x);
//            if (compareX == 0) {
//                return Double.compare(o1.y, o2.y);
//            } else {
//                return compareX;
//            }
//        });
//        while (iter.hasNext()) {
//            setExp.add(iter.next());
//        }
//        for (Point<String> stringPoint : setExp) {
//            System.out.println(stringPoint);
//        }

    private static <T> double calcDistBetweenPoints(QuadPoint<T> l, QuadPoint<T> r) {
        return Math.sqrt(Math.pow(r.x - l.x, 2) + Math.pow(r.y - l.y, 2));
    }


}


//    @Test
//    public void test1() {
//        //60.000000, 30.000000
//        //60.004497, 30.000000
//        double oneKmAngelLon = LatLonConverter.getLonAngelStepForMeters(60, 1000);
//        double oneKmAngelLat = LatLonConverter.getLatAngelStepForMeters(60, 1000);
//        int depth = 4;
//        double quadrantLenX = oneKmAngelLon / 2;
//        double quadrantLenY = oneKmAngelLat;
//
//        System.out.println(Math.sqrt(Math.pow(oneKmAngelLon * 8, 2) + Math.pow(oneKmAngelLat * 8, 2)));
//
//        QuadTree<Integer> tree = new QuadTree<>(
//                quadrantLenX,
//                quadrantLenY,
//                depth,
//                30 / 2,
//                60);
//        System.out.println(tree.printTree());
//
//        tree.add((30 / 2 + (quadrantLenX * 8)), 60 + (quadrantLenY * 8), 1);
//        System.out.println((30 / 2 + (quadrantLenX * 8)));
//        System.out.println(60 + (quadrantLenY * 8));
//
//        {
//            Iterator<Point<Integer>> iterator = tree.searchByRadius(30 / 2, 60, 0.10175);
//            List<Point<Integer>> list = new ArrayList<>();
//            iterator.forEachRemaining(list::add);
//            assertEquals(0, list.size());
//        }
//
//        {
//            Iterator<Point<Integer>> iterator = tree.searchByRadius(30 / 2, 60, 0.10175 + 0.00001);
//            List<Point<Integer>> list = new ArrayList<>();
//            iterator.forEachRemaining(list::add);
//            assertEquals(1, list.size());
//        }
////
////        System.out.println(LatLonConverter.distFromInMeters(
////                30, 60,
////                30 + oneKmAngelLon, 60 + oneKmAngelLat
////        ));
//    }
//
//
//
//
//
//
//
//    //    X          Y
//    //30.157595, 59.832923  30.175432, 59.832923
//    //30.157595, 59.823978  30.175432, 59.823978
//    // *
//    //    *     yandex = 1km    my = 1000.04412
//    //
//    // A     B
//    // C     D
//
//    @Test
//    public void deleteIt() {
//
//        double yleft = 30.157595 / 2;
//        double yRight = 30.175432 / 2;
//
//        System.out.println(LatLonConverter.distFromInMeters(
//                yleft, 59.832923,
//                yRight, 59.823978
//        ));
//
//        double oneKmX = LatLonConverter.getLonAngelStepForMeters(59.832923, 1000) / 2;
//        double oneKmY = LatLonConverter.getLatAngelStepForMeters(59.832923, 1000);
//        System.out.println("oneKmX = " + oneKmX);
//        System.out.println("oneKmY = " + oneKmY);
//        double AB = yRight - yleft;
//        double AC = 59.832923 - 59.823978;
//        System.out.println("AB = " + AB);
//        System.out.println("AC = " + AC);
//        System.out.println("AD = " + Math.sqrt(AB * AB + AC * AC));
//
//        // oneKmY = 0.008994115380793224
//        // AD =     0.019954237494829982
//        // AD =     0.012631415884613887
//    }




