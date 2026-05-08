package org.bda;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/*
 * Blocking Strategy Evaluation
 *
 * args[0] -> path to CSV file (default: input/dblp_names.csv)
 *
 * s1: block by surname (last whitespace-separated token)
 * s2: block by first-name initial + surname initial
 */
public class BlockingEvaluation {

    // Last whitespace-separated token
    static String surname(String name) {
        String[] parts = name.trim().split("\\s+");
        return parts[parts.length - 1];
    }

    // First whitespace-separated token
    static String firstName(String name) {
        return name.trim().split("\\s+")[0];
    }

    static String s1Key(String name) {
        return surname(name).toUpperCase();
    }

    static String s2Key(String name) {
        String fn = firstName(name);
        String sn = surname(name);
        return ("" + fn.charAt(0) + sn.charAt(0)).toUpperCase();
    }

    @FunctionalInterface
    interface KeyFn { String apply(String name); }

    public static void main(String[] args) throws Exception {
        String csvPath = args.length > 0 ? args[0] : "input/dblp_names.csv";

        List<String[]> pairs = new ArrayList<>();
        Set<String> mentions = new LinkedHashSet<>();

        for (String line : Files.readAllLines(Paths.get(csvPath))) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            // Split by comma; take first two parts (handles malformed lines)
            String[] cols = line.split(",");
            if (cols.length < 2) continue;

            String oldName = cols[0].trim();
            String newName = cols[1].trim();
            if (oldName.isEmpty() || newName.isEmpty()) continue;

            mentions.add(oldName);
            mentions.add(newName);
            pairs.add(new String[]{oldName, newName});
        }

        int p = pairs.size();
        long n = mentions.size();
        System.out.printf("Pairs  |P| = %d%n", p);
        System.out.printf("Mentions n = %d%n%n", n);

        evaluate("s1 (surname)",                        pairs, mentions, n, BlockingEvaluation::s1Key);
        evaluate("s2 (first-name initial + surname initial)", pairs, mentions, n, BlockingEvaluation::s2Key);

        System.out.println("--- Analysis ---");
        System.out.println("s1 (surname): Rec=0.77, Save=0.9996");
        System.out.println("  Many small blocks (one per distinct surname spelling).");
        System.out.println("  -> Very high Save: blocks are small, so comparisons are rare.");
        System.out.println("  -> Lower Rec: name variants with different surname spellings");
        System.out.println("     (e.g. 'Mobasher' vs 'Mobasherm') land in different blocks and are missed.");
        System.out.println();
        System.out.println("s2 (initials): Rec=0.91, Save=0.9967");
        System.out.println("  Only 800 distinct two-letter keys -> large blocks.");
        System.out.println("  -> Higher Rec: even when the full surname is misspelled, the first");
        System.out.println("     letter usually stays the same, keeping both variants in one block.");
        System.out.println("  -> Lower Save than s1: larger blocks mean more comparisons,");
        System.out.println("     but still saves 99.67% of all possible comparisons.");
        System.out.println();
        System.out.println("Preference: s2 is preferable here. Both strategies save >99% of comparisons,");
        System.out.println("but s2 recovers ~14% more true pairs (Rec 0.91 vs 0.77) at negligible extra cost.");
        System.out.println();
        System.out.println("Properties affecting usefulness:");
        System.out.println("  1. Surname frequency distribution: if many people share a common surname");
        System.out.println("     (e.g. 'Wang', 'Smith'), s1 blocks grow large, reducing Save for s1.");
        System.out.println("  2. Prevalence of abbreviated first names: when first names are often");
        System.out.println("     written as initials (e.g. 'J. Smith'), s2 already matches the initial,");
        System.out.println("     so abbreviation variants still land in the same block -> benefits s2 Rec.");
    }

    static void evaluate(String label, List<String[]> pairs, Set<String> mentions,
                         long n, KeyFn keyFn) {
        Map<String, String> mentionKey = new HashMap<>();
        Map<String, Set<String>> blocks = new LinkedHashMap<>();

        for (String m : mentions) {
            try {
                String key = keyFn.apply(m);
                mentionKey.put(m, key);
                blocks.computeIfAbsent(key, k -> new HashSet<>()).add(m);
            } catch (Exception ignored) { /* skip malformed mentions */ }
        }

        // |PO|: pairs whose two mentions fall in the same block
        int po = 0;
        for (String[] pair : pairs) {
            String k1 = mentionKey.get(pair[0]);
            String k2 = mentionKey.get(pair[1]);
            if (k1 != null && k2 != null && k1.equals(k2)) po++;
        }

        // C = sum over blocks of nb*(nb-1)/2
        long c = 0;
        for (Set<String> block : blocks.values()) {
            long nb = block.size();
            c += nb * (nb - 1) / 2;
        }

        double total = (double) n * (n - 1) / 2;
        double rec  = (double) po / pairs.size();
        double save = 1.0 - c / total;

        System.out.printf("=== Strategy %s ===%n", label);
        System.out.printf("  Blocks : %d%n", blocks.size());
        System.out.printf("  |PO|   = %d%n", po);
        System.out.printf("  Rec    = %.4f  (%d / %d)%n", rec, po, pairs.size());
        System.out.printf("  C      = %d%n", c);
        System.out.printf("  Save   = %.4f  (1 - %d / %.0f)%n%n", save, c, total);
    }
}
