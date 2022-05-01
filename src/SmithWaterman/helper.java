/**
 * Source: https://github.com/JayakrishnaThota/Sequence-Alignment
 */


package SmithWaterman;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class helper
{
	public String[] getrecords(String file) throws IOException {
		List<String> result = new ArrayList<>();
		final List<String> currentSeq = new ArrayList<>();
		lines(file).forEach(ln -> {
		if (ln.startsWith(">hsa:")) {
		String joinedSeq = currentSeq.stream().collect(Collectors.joining());
		result.add(joinedSeq);
		currentSeq.removeIf(s -> true);
		} else {
		currentSeq.add(ln);
		}
		});
		String joinedSeq = currentSeq.stream().collect(Collectors.joining());
		result.add(joinedSeq);
		result.remove(0);
		return result.toArray(new String[0]);
		}

	public int[][] getmatrix(String file) throws IOException {
        List<String> lines = lines(file).collect(Collectors.toList());
        int[][] arr = new int[lines.size()][];
        for (int i = 0; i < arr.length; i++) {
            String[] pieces = lines.get(i).split(" ");
            arr[i] = Arrays.stream(pieces).filter(str -> !str.isEmpty()).mapToInt(Integer::parseInt).toArray();
        }
        return arr;
    }

	 public String[] getids(String file) throws IOException {
	    	return lines(file)
	    	.filter(ln -> ln.startsWith(">hsa:"))
	    	.map(ln -> ln.substring(5))
	    	.map(ln -> ln.split(" "))
	    	.map(pieces -> pieces[0])
	    	.collect(Collectors.toList())
	    	.toArray(new String[0]);
	    	}

	public char[] readAsCharArray(String file) throws IOException {
        return getalphabet(file).toCharArray();
    }

    public String getalphabet(String file) throws IOException {
        return lines(file).collect(Collectors.joining());
    }

    public static Stream<String> lines(String file) throws IOException {
        return Files.lines(Paths.get(file));
    }
}
