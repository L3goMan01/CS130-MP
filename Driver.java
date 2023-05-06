import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Driver {
    public static String compareMinTerms(String binary1, String binary2) {
        int count = 0; // How many bits differ between the two binary numbers
        int location = 0; // At which bit location is that difference

        for (int i = 0; i < binary1.length(); i++) { // Checking for differences
            if (!(binary1.charAt(i)==binary2.charAt(i))) {
                count += 1; // If found a difference, add 1 to the count
                location = i; // Store the location of that difference for manipulation later
            }
        }

        if (count == 1) { // If and only if the bit difference is 1, perform manipulation
            StringBuilder newBinary = new StringBuilder(binary1);
            newBinary.setCharAt(location, '-'); // Using StringBuilder we can change the character at a given index to another character
            return newBinary.toString();
        }
        return null; // Return null if the bit difference count is not exactly 1
    }

    public static List<List<String>> cleanUpList(List<List<String>> list) {
        List<List<String>> cleaned = new ArrayList<>(list); // Make list with all minterms (which may contain duplicates)
        List<String> binaryStorage = new ArrayList<>(); // To store the binary numbers that we find, which will be used to determine duplicates

        for (List<String> curr : list) { // For each minterm in list, do...
            int size = curr.size(); // Gets the size of the current minterm (since they can be different based on number of minterms involved)
            if (!binaryStorage.contains(curr.get(size-1))) { // Checks the last element of the minterm list which contains the binary number
                binaryStorage.add(curr.get(size-1)); // Add that binary number assuming it does not already exist in the storage
            }
            else {
                cleaned.remove(curr); // If duplicate is found, remove that minterm from the list containing all minterms
            }
        }
        return cleaned; // Return list with all duplicates removed (if found any)
    }

    public static List<List<String>> findPrimeImplic(String[] minTermList, List<List<String>> minterms) {
        List<String> header = new ArrayList<>(Arrays.asList(minTermList)); // List to store the header which consist of the minterm numbers
        List<String[]> rowList = new ArrayList<>(); // List to keep lists for the rows
        int headerSize = header.size(); // Stores the count of how many columns in each row
        for (int i = 0; i < minterms.size(); i++) { // Filling the rows with blanks
            String[] term = new String[headerSize];
            for (int j = 0; j < headerSize; j++) {
                term[j] = " ";
            }
            rowList.add(term);
        }

        for (int i = 0; i < minterms.size(); i++) { // Placing the Xs in the rows
            int size = minterms.get(i).size(); // Size of the minterm list (used to single out the binary code which is the last element in that list)
            for (int j = 0; j < size-1; j++) {
                String[] curr = rowList.get(i); // Getting the ith row
                if (header.contains(minterms.get(i).get(j))) {
                    curr[header.indexOf(minterms.get(i).get(j))] = "X"; // Placing an X at the index of the corresponding minterm number from the header
                }
            }
        }

        // Printing the prime implicants table
        System.out.println("Finding the Prime Implicants:");
        StringBuilder headerStr = new StringBuilder();
        headerStr.append("Minterms").append("\t").append("|").append("\t");
        for (String head : header) {
            headerStr.append(head).append("\t"); // Appending header numbers to the header output
        }
        System.out.println(headerStr);
        System.out.println("=".repeat(70));

        for (int i = 0; i < rowList.size(); i++) { // For each row in rowList, do...
            StringBuilder rowStr = new StringBuilder();
            for (int x = 0; x < minterms.get(i).size()-1; x++) { // Adding the minterm numbers
                rowStr.append(minterms.get(i).get(x));
                if (x != minterms.get(i).size()-2) {
                    rowStr.append(",");
                }
            }
            // len is for spacing the minterm numbers to the X's
            int len = 1;
            if (rowStr.length()==1 || rowStr.length()==3) {
                len = 3;
            }
            else if (rowStr.length()==4 || rowStr.length()==5 || rowStr.length()==7) {
                len = 2;
            }
            rowStr.append("\t".repeat(len)).append("|").append("\t");
            for (int j = 0; j < rowList.get(i).length; j++) {
                String[] row = rowList.get(i);
                rowStr.append(row[j]).append("\t"); // Adding the X's
            }
            System.out.println(rowStr);
            System.out.println("-".repeat(70));
        }

        List<Integer> countArray = new ArrayList<>(); // List to keep track of how many X's are in each column
        for (int i = 0; i < header.size(); i++) {
            int count = 0;
            for (int j = 0; j < rowList.size(); j++) {
                String[] cur = rowList.get(j);
                if (cur[i] == "X") {
                    count += 1;
                }
            }
            countArray.add(count);
        }

        List<Integer> remainingTerms = new ArrayList<>(); // List to keep track the positions of the minterms which were not used
        for (int i = 0; i < minterms.size(); i++) {
            remainingTerms.add(i); // Filling the list with all positions
        }
        List<String> chosenTerms = new ArrayList<>(); // List to keep track of used minterms
        List<Integer> locationArray = new ArrayList<>(); // List to keep track of the positions of the minterms used
        // Note: ith Row in Table = ith Min term from minterm list
        // I'm using the position of the row in the list of rows as a way because it directly correlates to the ith term in the minterm list
        for (int i = 0; i < header.size(); i++) {
            if (countArray.get(i) == 1) {
                for (int j = 0; j < rowList.size(); j++) {
                    String[] cur = rowList.get(j);
                    if (cur[i] == "X") {
                        if (!locationArray.contains(j)) {
                            locationArray.add(j); // Storing the position of the used array
                        }
                        for (int x = 0; x < minterms.get(j).size()-1; x++) {
                            if (!chosenTerms.contains(minterms.get(j).get(x))) {
                                chosenTerms.add(minterms.get(j).get(x)); // Adding minterm numbers used, includes duplicates
                            }
                        }
                    }
                }
            }
        }

        remainingTerms.removeAll(locationArray); // Remove the used terms which only leaves the remaining terms
        List<String> missingTerms = new ArrayList<>(header); // Making a new tracking list for minterms
        missingTerms.removeAll(chosenTerms); // Removing the chosen terms after the first selection wave

        if (remainingTerms.size() > 0) { // If there are still terms to check, do...
            List<Integer> countCheck = new ArrayList<>();
            remainingTerms.forEach((pos) -> { // For each minterm index in remainingTerms do
                int count = 0; // Check how many terms overlap with the already chosen minterms
                int size = minterms.get(pos).size();
                for (int i = 0; i < size-1; i++) {
                    if (chosenTerms.contains(minterms.get(pos).get(i))) {
                        count += 1;
                    }
                }
                countCheck.add(count); // Stores the results
            });

            while (missingTerms.size() > 0) { // While there are still missing terms not chosen
                int lowestPos = countCheck.indexOf(Collections.min(countCheck)); // The lowest overlap count in countCheck
                int lowestTermPos = remainingTerms.get(lowestPos); // the index of that lowest overlap in the remainingTerms list
                List<String> lowestTerm = minterms.get(lowestTermPos); // getting that specific minterm
                for (int i = 0; i < lowestTerm.size()-1; i++) {
                    missingTerms.remove(lowestTerm.get(i)); // Removing the minterms from missing terms, since they are now found
                }
                locationArray.add(lowestTermPos); // Add the index of the lowest overlap minterm to the location array
                countCheck.remove(lowestPos); // Remove the lowest overlap count from the countCheck
                remainingTerms.remove(lowestPos); // Remove that found term from the terms to check
            }
        }

        List<List<String>> result = new ArrayList<>(); // Returning the list of minterms that passed the checks
        locationArray.forEach((pos) -> { // For each index in the location array add that minterm to the final list
            result.add(minterms.get(pos));
        });
        return result;
    }

    public static String convertTerm(List<String> term, String[] prefVar) {
        StringBuilder result = new StringBuilder(); // We use a StringBuilder so that we can keep appending to it
        int size = term.size(); // Gets the size of the minterm list so that we can get specific elements in it
        String binary = term.get(size-1); // Getting the last element of the list because it contains the binary representation
        for (int i = 0; i < binary.length(); i++) { // For each bit in the binary, do...
            if (binary.charAt(i)=='0') { // If bit is 0, then prime the letter
                result.append(prefVar[i] + "'");
            }
            else if (binary.charAt(i)=='1') { // If bit is 1, then leave unprimed
                result.append(prefVar[i]);
            }
        } // If bit is neither 0 nor 1, then ignore and skip
        return result.toString(); // Return the final term
    }

    public static void printTable(Map<Integer,List<List<String>>> map) {
        System.out.println("Group (# of 1's)" + "\t".repeat(2) + "Minterms" + "\t".repeat(5) + "Binary");
        System.out.println("=".repeat(70));
        map.forEach((key,value) -> { // For each key (# of 1's in binary) and value (list containing minterms)
            System.out.println(key + ":"); // Print count of 1's in binary to show group
            for (int i = 0; i < value.size(); i++) { // For each minterm in list
                StringBuilder print = new StringBuilder();
                int size = value.get(i).size();
                for (int j = 0; j < size-1; j++) {
                    print.append(value.get(i).get(j)); // Append minterm numbers
                    if (j != size-2) {
                        print.append(",");
                    }
                    else {
                        print.append("\t".repeat(6)).append(value.get(i).get(size-1)); // Add binary representation
                    }
                }
                System.out.println("\t".repeat(6) + print); // Print the resulting line
            }
            System.out.println("-".repeat(70));
        });
        System.out.println();
    }

    public static void main(String[] args) {

        System.out.println("\nTHE TABULATION METHOD\n");
        while (true) {

            System.out.print("Enter the minterms: ");
            Scanner sc = new Scanner(System.in); // Scanner to get user input
            String mt = sc.nextLine(); // String of minterms

            System.out.print("Enter preferred variables: ");
            String prefVar = sc.nextLine(); // String of variables

            List<List<String>> masterList = new ArrayList<>(List.of()); // List to store all the minterms and their respective binary representations

            String[] varList = prefVar.split(" "); // Splitting the string of variables by " " and putting into an array
            int numVar = varList.length; // How many variables the user put in

            String[] mt_list = mt.split(" "); // Splitting the string of minterms by " " and putting into an array
            
            for (String term : mt_list) { // For each minterm in the array, do...
                List<String> temp = new ArrayList<>(List.of()); // A list to store the min term data
                // NOTE: The general format for the minterms in this program are [minterm, binary representation]
                // The number of minterms can change as they are paired up later to find 1 bit differences
                // Because of the changing size, we use lists as they can be dynamically sized unlike arrays which need to be a static size
                int numTerm = Integer.parseInt(term); // Turning the minterm into an integer
                String newNumTerm = Integer.toBinaryString(numTerm); // Converiting that integer to binary

                int newNumTermBits = newNumTerm.length(); // How many bits the converted binary has

                if (newNumTermBits < numVar) { // If there are not enough bits, add padding 0's
                    int missingBits = (numVar - newNumTermBits); // Number of padding 0's to be added

                    StringBuilder finalBin = new StringBuilder();
                    // We use a StringBuilder here as we can continually keep adding any string to it, then return its result
                    for (int j = 0; j < missingBits; j++) {
                        finalBin.append("0");
                    }
                    finalBin.append(newNumTerm); // Appending the converted binary after placing the padding 0's

                    temp.add(term); // Add the minterm to the list
                    temp.add(finalBin.toString()); // Add the binary representation to the list
                }
                else { // If the required number of bits are filled, then simply add the minterm and its binary representation to the list
                    temp.add(term);
                    temp.add(newNumTerm);
                }
                masterList.add(temp); // Add that newly created minterm to the master list
            }

            AtomicInteger count = new AtomicInteger(); // Count to check if any pairs were found to have 1 bit diff
            AtomicInteger countBin = new AtomicInteger(); // Integer keeping track where in the minterm list the binary representation is
            // Note: We use AtomicInteger because a normal Integer cannot be used in a forEach loop
            countBin.set(1); // Set to 1 by default as at start list is of size 2 and element 1 is the last element
            List<List<String>> allTerms = new ArrayList<>(List.of()); // List containing all terms (seperate from masterList, as masterList will be used in the iterations of the next loop)
            List<List<String>> usedTerms = new ArrayList<>(List.of()); // List containing all the terms found to have a 1 bit difference

            while (true) {
                count.set(0); // Reset count of 1 bit difference binaries each loop
                Map<Integer, List<List<String>>> groupedBinary = masterList.stream().collect(Collectors.groupingBy(s -> Math.toIntExact(s.get(Integer.parseInt(countBin.toString())).chars().filter(ch -> ch == '1').count())));
                // What this map does is essentially go through each minterm in the masterList and sort them by how many 1's there are in its binary representation
                // The keys in the map are the number of 1's in the binary representations, while the value is a list of the minterm lists with the respective 1's count
                masterList.clear(); // Clear master list as it will be the one used to input back into the Map

                printTable(groupedBinary);

                ArrayList<Integer> keyList = new ArrayList<>(); // A list storing all the keys
                groupedBinary.forEach((key,value) -> keyList.add(key)); // Filling the list with the keys

                keyList.forEach((key) -> { // For each key, do...
                    for (int i = 0; i < groupedBinary.get(key).size(); i++) { // For each minterm list in the current group, do...
                        if (groupedBinary.get(key+1) != null) { // Making sure that there is a next group, if present do...
                            for (int j = 0; j < groupedBinary.get(key+1).size(); j++) { // For each minterm list in the next group, do...
                                // Storing the binary representations of a minterm in the current group then the next group
                                String bin1 = groupedBinary.get(key).get(i).get(Integer.parseInt(countBin.toString()));
                                String bin2 = groupedBinary.get(key+1).get(j).get(Integer.parseInt(countBin.toString()));
                                String comp = compareMinTerms(bin1,bin2);
                                // Goes to compareMinTerms method, which returns a new binary representation if there is a 1 bit difference
                                // returns null if the bit difference is not exactly 1

                                List<String> tempA = new ArrayList<>(); // Temporary list for one minterm
                                for (int y = 0; y <Integer.parseInt(countBin.toString()); y++) {
                                    tempA.add(groupedBinary.get(key).get(i).get(y)); // Adding each minterm to the list
                                }
                                tempA.add(bin1); // Add the binary representation of the above minterm
                                List<String> tempB = new ArrayList<>(); // Temporary list for one minterm
                                for (int y = 0; y <Integer.parseInt(countBin.toString()); y++) {
                                    tempB.add(groupedBinary.get(key+1).get(j).get(y)); // Adding each minterm to the list
                                }
                                tempB.add(bin2); // Add the binary representation of the above minterm
                                // tempA and tempB represent the two minterms that we just compared

                                if (!allTerms.contains(tempA)) { // if tempA is not already in allTerms, then add it
                                    allTerms.add(tempA);
                                }
                                if (!allTerms.contains(tempB)) { // if tempB is not already in allTerms, then add it
                                    allTerms.add(tempB);
                                }

                                if (comp != null) { // If the two binary representations compared have only 1 bit difference, do...
                                    if (!usedTerms.contains(tempA)) { // If tempA is not already in usedTerms, then add it
                                        usedTerms.add(tempA);
                                    }
                                    if (!usedTerms.contains(tempB)) { // If tempB is not already in usedTerms, then add it
                                        usedTerms.add(tempB);
                                    }

                                    count.addAndGet(1); // Adding 1 to count because a pair with 1 bit difference was found
                                    List<String> temp = new ArrayList<>(); // temp new list for adding minterm to masterList for next loop
                                    for (int x = 0; x < Integer.parseInt(countBin.toString()); x++) {
                                        temp.add(groupedBinary.get(key).get(i).get(x));
                                        temp.add(groupedBinary.get(key+1).get(j).get(x));
                                    } // getting numbers involved to make the pair
                                    temp.add(comp); // Add the new binary with '-' at the single bit difference location
                                    masterList.add(temp); // Add this pair to the masterList for next loop
                                    allTerms.add(temp); // Add this pair to allTerms to keep track of all terms
                                }
                            }
                        }
                    }
                });
                countBin.addAndGet(Integer.parseInt(countBin.toString())); // countBin has to scale exponentially as the size of the array grows

                if (count.get() == 0) { // If it found no minterms with 1 bit difference, we end the loop
                    break;
                }
            }

            allTerms.removeAll(usedTerms); // Removing the used terms from the list of all terms

            List<List<String>> finalMinTerms = cleanUpList(allTerms); // List after cleanUpList method, which removes the duplicates
            List<List<String>> afterEPI = findPrimeImplic(mt_list,finalMinTerms); // Finding the essential prime implicants

            StringBuilder epiStr = new StringBuilder();
            epiStr.append("Prime Implicants: ");
            for (int i = 0; i < afterEPI.size(); i++) {
                List<String> term = afterEPI.get(i);
                epiStr.append("(");
                int size = term.size();
                for (int j = 0; j < size-1; j++) {
                    epiStr.append(term.get(j));
                    if (j != size-2) {
                        epiStr.append(", ");
                    }
                    else {
                        if (i != afterEPI.size()-1) {
                            epiStr.append("), ");
                        }
                        else {
                            epiStr.append(")");
                        }
                    }
                }
            }
            System.out.println(epiStr + "\n");

            StringBuilder finalEquation = new StringBuilder(); // StringBuilder for making the final output which is the simplified equation
            finalEquation.append("F(");
            for (int i = 0; i < varList.length; i++) {
                finalEquation.append(varList[i]);
                if (i != varList.length-1) {
                    finalEquation.append(",");
                }
            }
            finalEquation.append(") = ");
            for (int i = 0; i < afterEPI.size(); i++) {
                finalEquation.append(convertTerm(afterEPI.get(i),varList));
                if (i != afterEPI.size()-1) {
                    finalEquation.append(" + ");
                }
            }

            System.out.println(finalEquation); // Print simplified equation

            System.out.println("Try another equation? (Y/N)"); // Ask user if they want to do this whole process again with a new equation
            String response = sc.nextLine().toLowerCase();
            if (response.equalsIgnoreCase("n")) {
                System.out.println("Thank you for using the program!");
                break;
            }
        }
    }
}