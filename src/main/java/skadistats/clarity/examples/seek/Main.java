package skadistats.clarity.examples.seek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skadistats.clarity.Clarity;
import skadistats.clarity.match.Match;
import skadistats.clarity.parser.DemoIndex;
import skadistats.clarity.parser.Profile;

public class Main {
    
    private static final int N_SEEKS = 100;

    public static void main(String[] args) throws Exception {

        long tStart = System.currentTimeMillis();

        Logger log = LoggerFactory.getLogger("TEST");

        DemoIndex idx = Clarity.indexForFile(args[0], Profile.ALL);

        long tIndex = System.currentTimeMillis() - tStart;
        log.info("index built in {}s", tIndex / 1000.0);

        tStart = System.currentTimeMillis();
        Match match = new Match(idx.prologueIterator());
        long tPrologue = System.currentTimeMillis() - tStart;
        log.info("prologue applied in {}s", tPrologue / 1000.0);

        tStart = System.currentTimeMillis();

        // this does several seek operations
        for (int c = 0; c < N_SEEKS; c++) {
            long tSkip = System.currentTimeMillis();
            int t = (int) (Math.random() * idx.getLastTick());
            match.reset();
            int nApplied = match.apply(idx.skipToIterator(t));
            log.info("restored to peek {} in {}s, visited {} packets", t, (System.currentTimeMillis() - tSkip) / 1000.0, nApplied);
        }

        long tMatch = System.currentTimeMillis() - tStart;
        log.info("{} seeks done in {}s, mean time {}s", N_SEEKS, tMatch / 1000.0, tMatch / 1000.0 / N_SEEKS);
        log.info("total time taken: {}s", (tIndex + tPrologue + tMatch) / 1000.0);
    }

}
