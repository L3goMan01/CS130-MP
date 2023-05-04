import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Driver {
    public static String compareMinTerms(String binary1, String binary2) {
        int count = 0;
        int location = 0;

        for (int i = 0; i < binary1.length(); i++) {
            if (!(binary1.charAt(i)==binary2.charAt(i))) {
                count += 1;
                location = i;
            }
        }

        if (count == 1) {
            StringBuilder newBinary = new StringBuilder(binary1);
            newBinary.setCharAt(location, '-');
            return newBinary.toString();
        }
        return null;
    }

    public static List<List<String>> cleanUpList(List<List<String>> list) {
        List<List<String>> cleaned = new ArrayList<>(list);
        List<String> binaryStorage = new ArrayList<>();

        for (List<String> curr : list) {
            int size = curr.size();
            if (!binaryStorage.contains(curr.get(size-1))) {
                binaryStorage.add(curr.get(size-1));
            } else {
                cleaned.remove(curr);
            }
        }
        return cleaned;
    }

    public static List<List<String>> findPrimeImplic(String[] minTermList, List<List<String>> minterms) {
        List<String> header = new ArrayList<>(Arrays.asList(minTermList)); // List to store the header (and minterm numbers)
        List<String[]> rowList = new ArrayList<>(); // List to keep lists for the rows
        int headerSize = header.size();
        for (int i = 0; i < minterms.size(); i++) { // Filling the rows with blanks
            String[] term = new String[headerSize];
            for (int j = 0; j < headerSize; j++) {
                term[j] = " ";
            }
            rowList.add(term);
        }

        for (int i = 0; i < minterms.size(); i++) { // Placing the Xs in the rows
            int size = minterms.get(i).size();
            for (int j = 0; j < size-1; j++) {
                String[] curr = rowList.get(i); // Getting the ith row
                if (header.contains(minterms.get(i).get(j))) {
                    curr[header.indexOf(minterms.get(i).get(j))] = "X"; // Placing an X at the index of the minterm number from the header
                }
            }
        }

        System.out.println(header); // Printing out the EPI Table (temp)
        for (String[] row : rowList) {
            System.out.println(Arrays.toString(row));
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
        System.out.println(countArray);

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
                            chosenTerms.add(minterms.get(j).get(x)); // Adding minterm numbers used, includes duplicates
                        }
                    }
                }
            }
        }

        remainingTerms.removeAll(locationArray); // Remove the used terms which only leaves the remaining terms

        System.out.println(locationArray);
        if (remainingTerms.size() > 1) {
            List<Integer> countCheck = new ArrayList<>();
            remainingTerms.forEach((pos) -> {
                int count = 0;
                int size = minterms.get(pos).size();
                for (int i = 0; i < size-1; i++) {
                    if (chosenTerms.contains(minterms.get(pos).get(i))) {
                        count += 1;
                    }
                }
                countCheck.add(count);
            });
            System.out.println(countCheck);
            locationArray.add(remainingTerms.get(countCheck.indexOf(Collections.min(countCheck))));
        }
        System.out.println(locationArray);
        List<List<String>> result = new ArrayList<>();
        locationArray.forEach((pos) -> {
            result.add(minterms.get(pos));
        });
        return result;
    }

    public static String convertTerm(List<String> term, String[] prefVar) {
        StringBuilder result = new StringBuilder();
        int size = term.size();
        String binary = term.get(size-1);
        for (int i = 0; i < binary.length(); i++) {
            if (binary.charAt(i)=='0') {
                result.append(prefVar[i] + "'");
            }
            else if (binary.charAt(i)=='1') {
                result.append(prefVar[i]);
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {

        System.out.println("\nTHE TABULATION METHOD\n");
        while (true) {

            System.out.print("Enter the minterms: ");
            Scanner sc = new Scanner(System.in);
            String mt = sc.nextLine();

            System.out.print("Enter preferred variables: ");
            String prefVar = sc.nextLine();

            List<List<String>> binaryList = new ArrayList<>(List.of());

            String[] varList = prefVar.split(" ");
            int numVar = varList.length;

            Map<Integer,String> storageMap = new HashMap<>();

            String[] mt_list = mt.split(" ");
            for (String term : mt_list) {
                List<String> temp = new ArrayList<>(List.of());
                int numTerm = Integer.parseInt(term);
                String newNumTerm = Integer.toBinaryString(numTerm);

                int newNumTermBits = newNumTerm.length();

                if (newNumTermBits < numVar) {
                    int missingBits = (numVar - newNumTermBits);

                    StringBuilder finalBin = new StringBuilder();
                    for (int j = 0; j < missingBits; j++) {
                        finalBin.append("0");
                    }
                    finalBin.append(newNumTerm);

                    System.out.println(numTerm + "\t" + finalBin);
                    storageMap.put(numTerm, finalBin.toString());
                    temp.add(term);
                    temp.add(finalBin.toString());
                } //end if
                else {
                    System.out.println(numTerm + "\t" + newNumTerm);
                    storageMap.put(numTerm, newNumTerm);
                    temp.add(term);
                    temp.add(newNumTerm);
                }
                binaryList.add(temp);
            } //end for
    //        System.out.println(binaryList);

            AtomicInteger count = new AtomicInteger(); // count to check if any pairs were found to have 1 bit diff
            AtomicInteger countBin = new AtomicInteger(); // integer keeping track where in the array the binary code is
            countBin.set(1);
            List<List<String>> allTerms = new ArrayList<>(List.of());
            List<List<String>> usedTerms = new ArrayList<>(List.of());

            while (true) {
                count.set(0); // reset count each loop
                System.out.println(countBin); // checked value every loop cuse i was worried lmao
                Map<Integer, List<List<String>>> groupedBinary = binaryList.stream().collect(Collectors.groupingBy(s -> Math.toIntExact(s.get(Integer.parseInt(countBin.toString())).chars().filter(ch -> ch == '1').count())));
                System.out.println("Grouped Numbers by 1's count" + groupedBinary);
                binaryList.clear(); // clear binary list as it will be the one used to input back into the Map

                ArrayList<Integer> keyList = new ArrayList<>();
                groupedBinary.forEach((key,value) -> keyList.add(key));

    //            System.out.println(keyList);
    //            keyList.forEach((key) -> System.out.println(groupedBinary.get(key)));

                keyList.forEach((key) -> {
                    for (int i=0; i < groupedBinary.get(key).size(); i++) {
                        if (groupedBinary.get(key+1) != null) {
                            for (int j=0; j < groupedBinary.get(key+1).size(); j++) {
                                String bin1 = groupedBinary.get(key).get(i).get(Integer.parseInt(countBin.toString())); // Getting the value at the countBin position (which should be the binary)
                                String bin2 = groupedBinary.get(key+1).get(j).get(Integer.parseInt(countBin.toString()));
                                String comp = compareMinTerms(bin1,bin2);

                                List<String> tempA = new ArrayList<>();
                                for (int y = 0; y <Integer.parseInt(countBin.toString()); y++) {
                                    tempA.add(groupedBinary.get(key).get(i).get(y));
                                }
                                tempA.add(bin1);

                                List<String> tempB = new ArrayList<>();
                                for (int y = 0; y <Integer.parseInt(countBin.toString()); y++) {
                                    tempB.add(groupedBinary.get(key+1).get(j).get(y));
                                }
                                tempB.add(bin2);
                                if (!allTerms.contains(tempA)) {
                                    allTerms.add(tempA);
                                }
                                if (!allTerms.contains(tempB)) {
                                    allTerms.add(tempB);
                                }

                                if (comp != null) {
                                    if (!usedTerms.contains(tempA)) {
                                        usedTerms.add(tempA);
                                    }
                                    if (!usedTerms.contains(tempB)) {
                                        usedTerms.add(tempB);
                                    }
                                    StringBuilder str = new StringBuilder(); // To build the (a,b,c...) part of print
                                    for (int s = 0; s < groupedBinary.get(key).get(i).size() - 1; s++) {
                                        str.append(groupedBinary.get(key).get(i).get(s));
                                        str.append(",");
                                        str.append(groupedBinary.get(key+1).get(j).get(s));
                                        if (s != (groupedBinary.get(key).get(i).size() - 2)) {
                                            str.append(",");
                                        }
                                    }
                                    System.out.println("(" + str + ") " + comp);
                                    count.addAndGet(1); // Adding 1 to count because pair is found
                                    List<String> temp = new ArrayList<>(); // temp new list for adding to binaryList for next loop
                                    for (int x = 0; x < Integer.parseInt(countBin.toString()); x++) {
                                        temp.add(groupedBinary.get(key).get(i).get(x));
                                        temp.add(groupedBinary.get(key+1).get(j).get(x));
                                    } // getting numbers involved to make pair
                                    temp.add(comp);
                                    binaryList.add(temp);
                                    allTerms.add(temp);
                                }
                            }
                        }
                    }
                });
                countBin.addAndGet(Integer.parseInt(countBin.toString())); // has to scale exponentially as the size of the array grows
    //            System.out.println("After loop: " + binaryList);

                if (count.get() == 0) {
                    break;
                }
            }
    //        System.out.println("all: " + allTerms);
    //        System.out.println("used: " + usedTerms);
            List<List<String>> union = new ArrayList<>(allTerms);
    //        union.addAll(usedTerms);
    //        List<List<String>> intersection = new ArrayList<>(allTerms);
    //        intersection.retainAll(usedTerms);
    //        union.removeAll(intersection);
            union.removeAll(usedTerms);
    //        System.out.println("union: " + union);
            List<List<String>> finalMinTerms = cleanUpList(union);
            List<List<String>> afterEPI = findPrimeImplic(mt_list,finalMinTerms);
            System.out.println("Final: " + finalMinTerms);
            System.out.println("AfterEPI: " + afterEPI);

            StringBuilder finalEquation = new StringBuilder();
            finalEquation.append("F(");
            for (int i = 0; i < varList.length; i++) {
                finalEquation.append(varList[i]);
                if (i!= varList.length-1) {
                    finalEquation.append(",");
                }
            }
            finalEquation.append(") = ");
            for (int i = 0; i < afterEPI.size(); i++) {
                finalEquation.append(convertTerm(afterEPI.get(i),varList));
                if (i!=afterEPI.size()-1) {
                    finalEquation.append(" + ");
                }
            }

            System.out.println(finalEquation);

            System.out.println("Try another equation? (Y/N)");
            String response = sc.nextLine().toLowerCase();
            if (response.toLowerCase().equals("n")) {
                System.out.println("Thank you for using the program!");
                break;
            }
        }
    } //end main
} //end Driver