package com.tourist_bot.quad;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class QuadTree<T> {

    private final Map<String, QuadLeaf<T>> quadrantToLeaf = new TreeMap<>();

    public final double quadrantLenX;
    public final double quadrantLenY;

    public final int quadDepth;
    public final double xMin;
    public final double yMin;

    private final QuadNode root;
    public final double quadTreeLenX;
    public final double xMax;
    public final double yMax;

    public final double quadTreeLenY;
    public final int quadrantsPerLineX;

    public static <T> QuadTree<T> buildByMinMax(double quadrantLenX, // quadrant min len
                                                double quadrantLenY,
                                                double startX,
                                                double endX,
                                                double startY) {
        return new QuadTree<>((float) quadrantLenX, (float) quadrantLenY, startX, endX, startY);
    }

    public static <T> QuadTree<T> buildByDepth(double quadrantLenX, // quadrant min len
                                               double quadrantLenY,
                                               int quadDepth, // depth
                                               double startX,
                                               double startY) {
        return new QuadTree<>(quadrantLenX, quadrantLenY, quadDepth, startX, startY);
    }

    private QuadTree(float quadrantLenX, // quadrant min len
                     float quadrantLenY,
                     double startX,
                     double endX,
                     double startY) {
        if (endX <= startX)
            throw new IllegalArgumentException("Quad tree endX coordinate should be greater than startX. Value '" + endX + "' is incorrect");
        this.quadrantLenX = quadrantLenX;
        this.quadrantLenY = quadrantLenY;
        this.xMin = startX;
        this.yMin = startY;
        int quadrantsPerLineXApprox = (int) Math.ceil((endX - startX) / quadrantLenX);

        this.quadDepth = (int) Math.ceil(Math.log(quadrantsPerLineXApprox) / Math.log(2));
        this.quadrantsPerLineX = (int) Math.pow(2, quadDepth);
        if (quadDepth < 1)
            throw new IllegalArgumentException("Quad depth should be greater than '0'. Value '" + quadDepth + "' is incorrect");

        this.quadTreeLenX = quadrantLenX * quadrantsPerLineX;
        this.quadTreeLenY = quadrantLenY * quadrantsPerLineX;
        this.xMax = startX + quadTreeLenX;
        this.yMax = startY + quadTreeLenY;
        this.root = new QuadNode(
                startX + quadTreeLenX / 2,
                startY + quadTreeLenY / 2,
                quadrantsPerLineX / 2,
                quadrantsPerLineX / 2,
                1);
    }

    private QuadTree(double quadrantLenX, // quadrant min len
                     double quadrantLenY,
                     int quadDepth, // depth
                     double initX, // x coordinate start pos
                     double initY) { // y coordinate start pos
        if (quadDepth < 1)
            throw new IllegalArgumentException("Quad depth should be greater than '0'. Value '" + quadDepth + "' is incorrect");
        this.quadrantLenX = quadrantLenX;
        this.quadrantLenY = quadrantLenY;
        this.quadDepth = quadDepth;
        this.xMin = initX;
        this.yMin = initY;
        this.quadrantsPerLineX = (int) Math.pow(2, quadDepth);
        this.quadTreeLenX = quadrantLenX * quadrantsPerLineX;
        this.quadTreeLenY = quadrantLenY * quadrantsPerLineX;
        this.xMax = initX + quadTreeLenX;
        this.yMax = initY + quadTreeLenY;
        this.root = new QuadNode(
                initX + quadTreeLenX / 2,
                initY + quadTreeLenY / 2,
                quadrantsPerLineX / 2,
                quadrantsPerLineX / 2,
                1);
    }


    public void add(double x, double y, T value) {
        if (x > xMin + quadTreeLenX || y > yMin + quadTreeLenY) {
            throw new IndexOutOfBoundsException();
        }

        if (x < xMin || y < yMin) {
            throw new IndexOutOfBoundsException();
        }

        int depth = 0;
        Quad currQuad = root;

        while (depth < quadDepth) {
            depth++;
            QuadNode quadNode = (QuadNode) currQuad;
            if (x < quadNode.midX && y >= quadNode.midY) { // WN
                if (quadNode.wn == null && depth == quadDepth) quadNode.createWnLeaf();
                if (quadNode.wn == null) quadNode.createWnNode(depth);
                currQuad = quadNode.wn;
            } else if (x >= quadNode.midX && y >= quadNode.midY) { // NE
                if (quadNode.ne == null && depth == quadDepth) quadNode.createNeLeaf();
                if (quadNode.ne == null) quadNode.createNeNode(depth);
                currQuad = quadNode.ne;
            } else if (x >= quadNode.midX && y < quadNode.midY) { // ES
                if (quadNode.se == null && depth == quadDepth) quadNode.createSeLeaf();
                if (quadNode.se == null) quadNode.createSeNode(depth);
                currQuad = quadNode.se;
            } else if (x < quadNode.midX && y < quadNode.midY) { // SW
                if (quadNode.ws == null && depth == quadDepth) quadNode.createWsLeaf();
                if (quadNode.ws == null) quadNode.createWsNode(depth);
                currQuad = quadNode.ws;
            } else {
            }
        }

        QuadLeaf<T> quadLeaf = (QuadLeaf<T>) currQuad;
        quadLeaf.data.add(new QuadPoint<>(x, y, value));
    }

    public Iterator<QuadPoint<T>> get(double x, double y, int radiusInQuads) {
        if (x > xMin + quadTreeLenX || y > yMin + quadTreeLenY) {
            throw new IndexOutOfBoundsException();
        }

        if (x < xMin || y < yMin) {
            throw new IndexOutOfBoundsException();
        }

        if (radiusInQuads < 1) {
            throw new InputMismatchException();
        }

        double radX = radiusInQuads * quadrantLenX;
        double radY = radiusInQuads * quadrantLenY;


        Iterator<QuadPoint<T>> iter = new Iterator<>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public QuadPoint<T> next() {
                return null;
            }
        };

        Stack<Quad> st = new Stack<>();
        st.push(root);
        while (!st.empty()) {
            Quad quad = st.pop();
            if (quad instanceof QuadTree.QuadNode) {
                QuadNode quadNode = (QuadNode) quad;
                if (x - radX < quadNode.midX && y + radY >= quadNode.midY) { // WN
                    if (quadNode.wn != null) st.push(quadNode.wn);
                }
                if (x + radX >= quadNode.midX && y + radY >= quadNode.midY) { // NE
                    if (quadNode.ne != null) st.push(quadNode.ne);
                }
                if (x + radX >= quadNode.midX && y - radY < quadNode.midY) { // ES
                    if (quadNode.se != null) st.push(quadNode.se);
                }
                if (x - radX < quadNode.midX && y - radY < quadNode.midY) { // SW
                    if (quadNode.ws != null) st.push(quadNode.ws);
                }
            } else {
                Iterator<QuadPoint<T>> all = ((QuadLeaf) quad).getAll();
                iter = concatIterator(all, iter);
            }
        }


        return iter;
    }


    public Iterator<QuadPoint<T>> searchInSiblingQuads(double x, double y, int numberOfSiblingQuads) {
        if (x > xMin + quadTreeLenX || y > yMin + quadTreeLenY) {
            throw new IndexOutOfBoundsException();
        }

        if (x < xMin || y < yMin) {
            throw new IndexOutOfBoundsException();
        }

        if (numberOfSiblingQuads < 0) {
            throw new InputMismatchException();
        }

        int xQuadLeafNumber = (int) ((x - xMin) / quadrantLenX);
        int yQuadLeafNumber = (int) ((y - yMin) / quadrantLenY);
        double xQuadLeafCenter = xMin + (xQuadLeafNumber * quadrantLenX) + (quadrantLenX / 2);
        double yQuadLeafCenter = yMin + (yQuadLeafNumber * quadrantLenY) + (quadrantLenY / 2);

        return search(xQuadLeafCenter, yQuadLeafCenter, numberOfSiblingQuads);
    }

    private Iterator<QuadPoint<T>> search(double x, double y, int numberOfSiblingQuads) {
        double radX = numberOfSiblingQuads * quadrantLenX;
        double radY = numberOfSiblingQuads * quadrantLenY;
        Iterator<QuadPoint<T>> iter = new Iterator<>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public QuadPoint<T> next() {
                return null;
            }
        };

        Stack<Quad> st = new Stack<>();
        st.push(root);
        while (!st.empty()) {
            Quad quad = st.pop();
            if (quad instanceof QuadTree.QuadNode) {
                QuadNode quadNode = (QuadNode) quad;
                if (x - radX < quadNode.midX && y + radY >= quadNode.midY) { // WN
                    if (quadNode.wn != null) st.push(quadNode.wn);
                }
                if (x + radX >= quadNode.midX && y + radY >= quadNode.midY) { // NE
                    if (quadNode.ne != null) st.push(quadNode.ne);
                }
                if (x + radX >= quadNode.midX && y - radY < quadNode.midY) { // ES
                    if (quadNode.se != null) st.push(quadNode.se);
                }
                if (x - radX < quadNode.midX && y - radY < quadNode.midY) { // SW
                    if (quadNode.ws != null) st.push(quadNode.ws);
                }
            } else {
                Iterator<QuadPoint<T>> all = ((QuadLeaf) quad).getAll();
                iter = concatIterator(all, iter);
            }
        }

        return iter;
    }

    public Iterator<QuadPoint<T>> searchByRadius(double x, double y, double radius) {
        List<QuadLeaf<T>> leafs = searchQuadrantsByRadius(x, y, radius);

        Function<QuadPoint<T>, Boolean> filter = e -> {
            double xDist = Math.pow(x - e.x, 2);
            double yDist = Math.pow(y - e.y, 2);
            double dist = Math.sqrt(xDist + yDist);
            boolean c = dist <= radius;
            return c;
        };

        Iterator<QuadPoint<T>> res = new Iterator<>() {
            final Iterator<QuadLeaf<T>> iterator = leafs.iterator();
            Iterator<QuadPoint<T>> pointIterator = iterator.hasNext() ? iterator.next().getAll() : null;

            @Override
            public boolean hasNext() {
                return (pointIterator != null && pointIterator.hasNext());
            }

            @Override
            public QuadPoint<T> next() {
                try {
                    return pointIterator.next();
                } finally {
                    if (!pointIterator.hasNext()) {
                        if (iterator.hasNext()) {
                            pointIterator = iterator.next().getAll();
                        } else {
                            pointIterator = null;
                        }
                    }
                }
            }
        };

        return new FilterIterator<>(
                res,
                filter);
    }


    List<QuadLeaf<T>> searchQuadrantsByRadius(double x, double y, double radius) {
        if (x - radius > xMax || x + radius < xMin) return new LinkedList<>();
        if (y - radius > yMax || y + radius < yMin) return new LinkedList<>();

        Stack<Quad> st = new Stack<>();
        List<QuadLeaf<T>> leafs = new LinkedList<>();
        st.push(root);
        while (!st.empty()) {
            Quad quad = st.pop();
            if (quad instanceof QuadTree.QuadNode) {
                QuadNode parentNode = (QuadNode) quad;
                if (x - radius <= parentNode.midX && y + radius >= parentNode.midY) { // WN
                    if (parentNode.wn != null) st.push(parentNode.wn);
                }
                if (x + radius >= parentNode.midX && y + radius >= parentNode.midY) { // NE
                    if (parentNode.ne != null) st.push(parentNode.ne);
                }
                if (x + radius >= parentNode.midX && y - radius <= parentNode.midY) { // ES
                    if (parentNode.se != null) st.push(parentNode.se);
                }
                if (x - radius <= parentNode.midX && y - radius <= parentNode.midY) { // SW
                    if (parentNode.ws != null) st.push(parentNode.ws);
                }
            } else {
                leafs.add((QuadLeaf) quad);
            }
        }

        return leafs;
    }


    static abstract class Quad {
    }

    /*
     *  			N
     *  		W		E
     *  			S
     *
     *      wn  ne
     *      sw  es
     */
    class QuadNode extends Quad {

        public final double midX;
        public final double midY;

        public final int xQuadId;
        public final int yQuadId;

        public final int depth;

        private Quad wn;
        private Quad ne;
        private Quad se;
        private Quad ws;

        public QuadNode(double midX,
                        double midY,
                        int xQuadId,
                        int yQuadId,
                        int depth) {
            this.midX = midX;
            this.midY = midY;
            this.xQuadId = xQuadId;
            this.yQuadId = yQuadId;
            this.depth = depth;
        }

        public void createWnNode(int depth) {
            int pow = (int) Math.pow(2, quadDepth - depth - 1);
            double stepX = quadrantLenX * pow;
            double stepY = quadrantLenY * pow;
            this.wn = new QuadNode(
                    midX - stepX,
                    midY + stepY,
                    xQuadId - pow,
                    yQuadId + pow,
                    depth + 1);
        }

        public void createNeNode(int depth) {
            int pow = (int) Math.pow(2, quadDepth - depth - 1);
            double stepX = quadrantLenX * pow;
            double stepY = quadrantLenY * pow;
            this.ne = new QuadNode(
                    midX + stepX,
                    midY + stepY,
                    xQuadId + pow,
                    yQuadId + pow,
                    depth + 1);
        }

        public void createSeNode(int depth) {
            int pow = (int) Math.pow(2, quadDepth - depth - 1);
            double stepX = quadrantLenX * pow;
            double stepY = quadrantLenY * pow;
            this.se = new QuadNode(
                    midX + stepX,
                    midY - stepY,
                    xQuadId + pow,
                    yQuadId - pow,
                    depth + 1);
        }

        public void createWsNode(int depth) {
            int pow = (int) Math.pow(2, quadDepth - depth - 1);
            double stepX = quadrantLenX * pow;
            double stepY = quadrantLenY * pow;
            this.ws = new QuadNode(
                    midX - stepX,
                    midY - stepY,
                    xQuadId - pow,
                    yQuadId - pow,
                    depth + 1);
        }

        public void createWnLeaf() {
//            int pow = (int) Math.pow(2, quadDepth - depth - 1);
            QuadLeaf<T> leaf = new QuadLeaf<>(xQuadId - 1, yQuadId);
            this.wn = leaf;
            if (quadrantToLeaf.containsKey(leaf.xQuadId + ":" + leaf.yQuadId)) {
                System.out.println();
            }
            quadrantToLeaf.put(leaf.xQuadId + ":" + leaf.yQuadId, leaf);
        }

        public void createNeLeaf() {
            QuadLeaf<T> leaf = new QuadLeaf<>(xQuadId, yQuadId);
            this.ne = leaf;
            if (quadrantToLeaf.containsKey(leaf.xQuadId + ":" + leaf.yQuadId)) {
                System.out.println();
            }
            quadrantToLeaf.put(leaf.xQuadId + ":" + leaf.yQuadId, leaf);
        }

        public void createSeLeaf() {
            QuadLeaf<T> leaf = new QuadLeaf<>(xQuadId, yQuadId - 1);
            this.se = leaf;
            if (quadrantToLeaf.containsKey(leaf.xQuadId + ":" + leaf.yQuadId)) {
                System.out.println();
            }
            quadrantToLeaf.put(leaf.xQuadId + ":" + leaf.yQuadId, leaf);
        }

        public void createWsLeaf() {
            QuadLeaf<T> leaf = new QuadLeaf<>(xQuadId - 1, yQuadId - 1);
            this.ws = leaf;
            if (quadrantToLeaf.containsKey(leaf.xQuadId + ":" + leaf.yQuadId)) {
                System.out.println();
            }
            quadrantToLeaf.put(leaf.xQuadId + ":" + leaf.yQuadId, leaf);
        }

    }

    static class QuadLeaf<T> extends Quad {

        public final Set<QuadPoint<T>> data;

        public final long xQuadId;
        public final long yQuadId;

        public QuadLeaf(long xQuadId, long yQuadId) {
            this.xQuadId = xQuadId;
            this.yQuadId = yQuadId;
            this.data = new TreeSet<>((o1, o2) -> {
                int compare = Double.compare(o1.x, o2.x);
                if (compare == 0) {
                    return Double.compare(o1.y, o2.y);
                } else {
                    return compare;
                }
            });
        }

        public Iterator<QuadPoint<T>> getAll() {
            return data.iterator();
        }

        @Override
        public String toString() {
            return "X:" + xQuadId + " Y:" + yQuadId + " " + data.stream().map(QuadPoint::toString).collect(Collectors.joining(","));
        }
    }

    public static <T> Iterator<T> concatIterator(Iterator<T> it1, Iterator<T> it2) {
        return new Iterator<>() {
            public boolean hasNext() {
                return it1.hasNext() || it2.hasNext();
            }

            public T next() {
                return it1.hasNext() ? it1.next() : it2.next();
            }
        };
    }

    public String printTree() {
        return "QuadTree:\n"
                + "     yMax: " + yMax + "          ****Since 1 angel len is different from 0 to 180 calculated yMax could be bigger then 180 \n"
                + "     yMin: " + yMin + "\n"
                + "     xMin: " + xMin + "    xMax: " + xMax + "\n"
                + "         xQuadrantLen: " + quadrantLenX + "\n"
                + "         yQuadrantLen: " + quadrantLenY + "\n"
                + "         Depth: " + quadDepth + "\n"
                + "         NumberOfQuads per line: " + quadrantsPerLineX;
    }

}