/*******************************************************
 * @file: Proj3.java
 * @description: Runs different sorting algorithms on a
 *               Movie dataset. For the chosen algorithm,
 *               it sorts already-sorted, shuffled, and
 *               reversed lists, times them, and counts
 *               comparisons for Bubble Sort and Odd-Even
 *               Transposition Sort. Results go to the
 *               screen and analysis.txt.
 * @author: Sebastien Pierre
 * @date: November 13, 2025
 *******************************************************/
import java.io.*;
import java.util.*;

public class Proj3 {

    //merge sort

    // Merge sort on subarray [left, right]
    public static <T extends Comparable<? super T>>
    void mergeSort(ArrayList<T> a, int left, int right) {
        if (left >= right) return;
        int mid = (left + right) / 2;
        mergeSort(a, left, mid);
        mergeSort(a, mid + 1, right);
        merge(a, left, mid, right);
    }

    // Merge two sorted halves
    public static <T extends Comparable<? super T>>
    void merge(ArrayList<T> a, int left, int mid, int right) {
        ArrayList<T> temp = new ArrayList<>();

        int i = left;
        int j = mid + 1;

        // merge elements into temp
        while (i <= mid && j <= right) {
            if (a.get(i).compareTo(a.get(j)) <= 0) {
                temp.add(a.get(i));
                i++;
            } else {
                temp.add(a.get(j));
                j++;
            }
        }

        // copy any leftovers
        while (i <= mid) {
            temp.add(a.get(i));
            i++;
        }
        while (j <= right) {
            temp.add(a.get(j));
            j++;
        }

        // copy back into original list
        for (int k = 0; k < temp.size(); k++) {
            a.set(left + k, temp.get(k));
        }
    }

    //quick sort

    // Quick sort using last element as pivot
    public static <T extends Comparable<? super T>>
    void quickSort(ArrayList<T> a, int left, int right) {
        if (left < right) {
            int p = partition(a, left, right);
            quickSort(a, left, p - 1);
            quickSort(a, p + 1, right);
        }
    }

    // Partition (Lomuto)
    public static <T extends Comparable<? super T>>
    int partition(ArrayList<T> a, int left, int right) {
        T pivot = a.get(right);
        int i = left - 1;
        for (int j = left; j < right; j++) {
            if (a.get(j).compareTo(pivot) <= 0) {
                i++;
                swap(a, i, j);
            }
        }
        swap(a, i + 1, right);
        return i + 1;
    }

    // Simple swap helper
    static <T> void swap(ArrayList<T> a, int i, int j) {
        T temp = a.get(i);
        a.set(i, a.get(j));
        a.set(j, temp);
    }

    //heap

    // Heap sort on the whole list (we assume left=0, right=size-1)
    public static <T extends Comparable<? super T>>
    void heapSort(ArrayList<T> a, int left, int right) {
        if (right <= left) return;

        int n = right - left + 1;   // number of elements, indices 0..n-1

        // build max heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(a, i, n - 1);
        }

        // pull max to end and shrink heap
        for (int end = n - 1; end > 0; end--) {
            swap(a, 0, end);
            heapify(a, 0, end - 1);
        }
    }

    // Heapify subtree rooted at "root" within 0..end
    public static <T extends Comparable<? super T>>
    void heapify(ArrayList<T> a, int root, int end) {
        while (true) {
            int leftChild  = 2 * root + 1;
            int rightChild = 2 * root + 2;
            int largest = root;

            if (leftChild <= end &&
                    a.get(leftChild).compareTo(a.get(largest)) > 0) {
                largest = leftChild;
            }
            if (rightChild <= end &&
                    a.get(rightChild).compareTo(a.get(largest)) > 0) {
                largest = rightChild;
            }

            if (largest != root) {
                swap(a, root, largest);
                root = largest;
            } else {
                break;
            }
        }
    }

    //bubble sort

    // Bubble sort, returns number of comparisons
    public static <T extends Comparable<? super T>>
    int bubbleSort(ArrayList<T> a, int size) {
        int comparisons = 0;
        boolean swapped;

        for (int pass = 0; pass < size - 1; pass++) {
            swapped = false;
            for (int i = 0; i < size - 1 - pass; i++) {
                comparisons++;
                if (a.get(i).compareTo(a.get(i + 1)) > 0) {
                    swap(a, i, i + 1);
                    swapped = true;
                }
            }
            // if nothing swapped, list is already sorted
            if (!swapped) break;
        }
        return comparisons;
    }

    //odd even transition sort

    public static <T extends Comparable<? super T>>
    int transpositionSort(ArrayList<T> a, int size) {
        boolean isSorted = false;
        int steps = 0;

        while (!isSorted) {
            isSorted = true;

            // odd indexed pairs
            for (int i = 1; i <= size - 2; i += 2) {
                if (a.get(i).compareTo(a.get(i + 1)) > 0) {
                    swap(a, i, i + 1);
                    isSorted = false;
                }
            }
            steps++; // one parallel odd step

            // even indexed pairs
            for (int i = 0; i <= size - 2; i += 2) {
                if (a.get(i).compareTo(a.get(i + 1)) > 0) {
                    swap(a, i, i + 1);
                    isSorted = false;
                }
            }
            steps++; // one parallel even step
        }
        return steps;
    }

    //read data

    private static ArrayList<Movie> readMovies(String path, int limit) {
        ArrayList<Movie> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null && list.size() < limit) {
                // skip header
                String lower = line.toLowerCase();
                if (first && lower.contains("title") && lower.contains("year")) {
                    first = false;
                    continue;
                }
                first = false;

                String[] p = line.split(",");
                if (p.length < 4) continue;

                String title  = p[0].trim();
                int year      = safeInt(p[1]);
                double rating = safeDouble(p[2]);
                int votes     = safeInt(p[3]);

                list.add(new Movie(title, year, rating, votes));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return list;
    }

    private static int safeInt(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }

    private static double safeDouble(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (Exception e) { return 0.0; }
    }

    //CSV

    // Add one line to analysis.txt
    private static void appendCsv(String filename, int n,
                                  String alg, String order,
                                  long timeNs, int comparisons) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename, true))) {
            // N,algorithm,order,timeNs,comparisons
            out.printf(Locale.US, "%d,%s,%s,%d,%d%n",
                    n, alg, order, timeNs, comparisons);
        } catch (IOException e) {
            System.err.println("Error writing " + filename + ": " + e.getMessage());
        }
    }

    //Sorted Lists

    // Overwrite sorted.txt with the already sorted input result
    private static void writeSortedList(String filename,
                                        String alg,
                                        String order,
                                        List<Movie> list) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename, false))) {
            out.println("Sorted list using " + alg + " sort (" + order + " input):");
            for (Movie m : list) {
                out.println(m);
            }
        } catch (IOException e) {
            System.err.println("Error writing " + filename + ": " + e.getMessage());
        }
    }

    //main

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: java Proj3 <dataset-file> <algorithm> <number-of-lines>");
            System.err.println("algorithm = bubble | merge | quick | heap | transposition");
            System.exit(1);
        }

        String filename = args[0];
        String alg = args[1].toLowerCase();
        int n = Integer.parseInt(args[2]);

        // read base data
        ArrayList<Movie> base = readMovies(filename, n);
        if (base.isEmpty()) {
            System.err.println("No data read. Check file path and N.");
            System.exit(1);
        }

        // make three versions: sorted, shuffled, reversed
        ArrayList<Movie> sorted = new ArrayList<>(base);
        Collections.sort(sorted);

        ArrayList<Movie> shuffled = new ArrayList<>(sorted);
        Collections.shuffle(shuffled);

        ArrayList<Movie> reversed = new ArrayList<>(sorted);
        Collections.sort(reversed, Collections.reverseOrder());

        // copies used as input so we don't mutate originals
        ArrayList<Movie> s1 = new ArrayList<>(sorted);
        ArrayList<Movie> s2 = new ArrayList<>(shuffled);
        ArrayList<Movie> s3 = new ArrayList<>(reversed);

        long t1, t2, t3;
        int c1 = -1, c2 = -1, c3 = -1;

     //already sorted input
        long start = System.nanoTime();
        if (alg.equals("merge")) mergeSort(s1, 0, s1.size() - 1);
        else if (alg.equals("quick")) quickSort(s1, 0, s1.size() - 1);
        else if (alg.equals("heap")) heapSort(s1, 0, s1.size() - 1);
        else if (alg.equals("bubble")) c1 = bubbleSort(s1, s1.size());
        else if (alg.equals("transposition")) c1 = transpositionSort(s1, s1.size());
        else {
            System.err.println("Unknown algorithm: " + alg);
            System.exit(1);
        }
        t1 = System.nanoTime() - start;

        //shuffled
        start = System.nanoTime();
        if (alg.equals("merge")) mergeSort(s2, 0, s2.size() - 1);
        else if (alg.equals("quick")) quickSort(s2, 0, s2.size() - 1);
        else if (alg.equals("heap")) heapSort(s2, 0, s2.size() - 1);
        else if (alg.equals("bubble")) c2 = bubbleSort(s2, s2.size());
        else if (alg.equals("transposition")) c2 = transpositionSort(s2, s2.size());
        t2 = System.nanoTime() - start;

        //reversed
        start = System.nanoTime();
        if (alg.equals("merge")) mergeSort(s3, 0, s3.size() - 1);
        else if (alg.equals("quick")) quickSort(s3, 0, s3.size() - 1);
        else if (alg.equals("heap")) heapSort(s3, 0, s3.size() - 1);
        else if (alg.equals("bubble")) c3 = bubbleSort(s3, s3.size());
        else if (alg.equals("transposition")) c3 = transpositionSort(s3, s3.size());
        t3 = System.nanoTime() - start;

        //print
        System.out.println("------------------");
        System.out.println("Algorithm: " + alg);
        System.out.println("N = " + n);
        System.out.println();

        System.out.println("Already-sorted input:");
        System.out.println("  Time (ns): " + t1);
        if (c1 >= 0) System.out.println("  Comparisons/steps: " + c1);

        System.out.println("Shuffled input:");
        System.out.println("  Time (ns): " + t2);
        if (c2 >= 0) System.out.println("  Comparisons/steps: " + c2);

        System.out.println("Reversed input:");
        System.out.println("  Time (ns): " + t3);
        if (c3 >= 0) System.out.println("  Comparisons/steps: " + c3);

        System.out.println("----------------------");

     //analysis txt
        appendCsv("analysis.txt", n, alg, "sorted",   t1, c1);
        appendCsv("analysis.txt", n, alg, "shuffled", t2, c2);
        appendCsv("analysis.txt", n, alg, "reversed", t3, c3);

        //sorted txt
        writeSortedList("sorted.txt", alg, "sorted", s1);
    }
}
