package accumulator;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class StringAccumulator {

    public static int add(String numbers) {

        if (StringUtils.isEmpty(numbers)) {
            return 0;
        }

        List<String> delimiters = Lists.newArrayList();

        String[] rows = numbers.split("\n");
        boolean delimiterProvided = findDelimiter(numbers, delimiters, rows);

        List<Integer> negaitveNumbers = Lists.newArrayList();

        int currentSum = 0;
        // skip delimiter row if provided
        for (int i = delimiterProvided ? 1 : 0; i < rows.length; i++) {
            String curInput = rows[i];
            currentSum += getNumberFromDelimiter(curInput, null, delimiters, negaitveNumbers);
        }

        if (!negaitveNumbers.isEmpty()) {
            throw new RuntimeException("negatives not allowed: " + negaitveNumbers.toString());
        }

        return currentSum;
    }

    private static boolean findDelimiter(String numbers, List<String> delimiters, String[] rows) {
        boolean delimiterProvided = false;
        // find the delimiters
        if (numbers.startsWith("//")) {
            String delimiterRow = rows[0];
            String[] delimiterInput = delimiterRow.substring(2).split("\\|");
            for (String delimiter : delimiterInput) {
                if (delimiter.contains("*")) {
                    String escapedDelimiter = delimiter.replace("*", "\\*");
                    delimiters.add(escapedDelimiter);
                } else {
                    delimiters.add(delimiter);
                }
            }
            delimiterProvided = true;
        } else {
            delimiters.add(",");
        }
        return delimiterProvided;
    }

    private static int getNumberFromDelimiter(String input, String curDelimiter, List<String> delimiters,
            List<Integer> negaitveNumbers) {
        int sum = 0;
        boolean delimiterFound = false;
        for (String delimiter : delimiters) {
            if (curDelimiter != delimiter) {
                String[] splitByDelimiter = input.split(delimiter);
                if (splitByDelimiter.length > 1) {
                    for (String possibleNumber : splitByDelimiter) {

                        //                        System.out.println(
                        //                                String.format("input: %s, delimiter: %s, possibleNumber: %s", input, delimiter, possibleNumber));
                        try {
                            int number = Integer.valueOf(possibleNumber);
                            if (number < 0) {
                                negaitveNumbers.add(number);
                            } else if (number > 1000) {
                                number = 0;
                            }
                            //                            System.out.println(String.format("sum: %s, numberToAdd: %s", sum, number));
                            sum += number;
                        } catch (NumberFormatException ex) {
                            // not a number
                            // recursively process this portion as it might have other delimiter
                            int number = getNumberFromDelimiter(possibleNumber, curDelimiter, delimiters, negaitveNumbers);
                            //                            System.out.println(String.format("sum: %s, numberToAdd: %s", sum, number));
                            sum += number;
                        }
                        input = input.replace(possibleNumber, "");
                    }
                    delimiterFound = true;
                }
            }
        }

        if (!delimiterFound) {
            int number = Integer.valueOf(input);
            if (number < 0) {
                negaitveNumbers.add(number);
            }
            //            System.out.println(String.format("input: %s, sum: %s, numberToAdd: %s", input, sum, number));
            sum += number;
        }

        return sum;
    }

    public static void main(String[] args) {

        List<String> input = Lists.newArrayList("", "1,2", "1\n2,3", "//;\n1;2", "//***\n1***2***3", "//*|%\n1*2%3",
                "//*|%\n1*2%30000", "//;;|'\n1\n2;;3'5;;4", "//'|;;\n1\n2;;3'5;;4", "//'|;;\n1\n2;;3'-5;;-4");

        for (String number : input) {
            System.out.println(String.format("input: %s, result: %s", number, StringAccumulator.add(number)));
        }
    }

}
