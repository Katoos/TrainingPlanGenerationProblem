import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

// Assumption : Non-complete weeks are excluded

public class TrainingPlanGenerator {
    public static void main(String[] args) {
        // Define the start date and race date
        LocalDate startDate = LocalDate.of(2021, 6, 6);
        LocalDate raceDate = LocalDate.of(2021, 8, 7);

        // Generate the training plan
        List<String> trainingPlan = generateTrainingPlan(startDate, raceDate);

        // Print the training plan
        printTrainingPlan(trainingPlan);
    }

    /**
     * Generates a training plan based on the start date and race date.
     *
     * @param startDate the start date of the training plan
     * @param raceDate  the race date
     * @return the generated training plan
     */
    public static List<String> generateTrainingPlan(LocalDate startDate, LocalDate raceDate) {
        // Calculate the total number of days and weeks between the start date and race date
        long totalDays = ChronoUnit.DAYS.between(startDate, raceDate) + 1;
        long totalWeeks = totalDays / 7;

        // Check if the total number of weeks is less than the minimum required
        if (totalWeeks < 8) {
            // Return an error plan if the total number of weeks is insufficient
            return createErrorPlan("Error: The total number of weeks must be at least 8.");
        }

        // Calculate the number of filler weeks and main block cycles
        int fillerWeeks = calculateFillerWeeks(totalWeeks);
        int mainBlockCycle = calculateMainBlockCycle(totalWeeks, fillerWeeks);

        // Generate the week types for the training plan
        List<String> weekTypes = generateWeekTypes(totalWeeks, fillerWeeks, mainBlockCycle);

        // Generate the plan based on the start date and week types
        List<String> plan = generatePlan(startDate, weekTypes);

        return plan;
    }

    /**
     * Calculates the number of filler weeks based on the total weeks.
     *
     * @param totalWeeks the total number of weeks
     * @return the number of filler weeks
     */
    private static int calculateFillerWeeks(long totalWeeks) {
        return (totalWeeks % 8 == 1 || totalWeeks % 8 == 5) ? 1 : 0;
    }

    /**
     * Calculates the number of main block cycles based on the total weeks and filler weeks.
     *
     * @param totalWeeks   the total number of weeks
     * @param fillerWeeks  the number of filler weeks
     * @return the number of main block cycles
     */
    private static int calculateMainBlockCycle(long totalWeeks, int fillerWeeks) {
        return (int) ((totalWeeks - fillerWeeks - 4) / 4);
    }

    /**
     * Generates the week types for the training plan.
     *
     * @param totalWeeks      the total number of weeks
     * @param fillerWeeks     the number of filler weeks
     * @param mainBlockCycle  the number of main block cycles
     * @return the list of week types
     */
    private static List<String> generateWeekTypes(long totalWeeks, int fillerWeeks, int mainBlockCycle) {
        List<String> weekTypes = new ArrayList<>();

        // Add test weeks
        addTestWeeks(weekTypes);

        // Add filler weeks
        addFillerWeeks(weekTypes, fillerWeeks);

        // Add additional main block weeks based on the total weeks
        addAdditionalWeeks(weekTypes, totalWeeks);

        // Add main block weeks
        addMainBlockWeeks(weekTypes, mainBlockCycle);

        // Add taper and race weeks
        weekTypes.add("Taper");
        weekTypes.add("Race");

        return weekTypes;
    }

    /**
     * Adds the test weeks to the week types list.
     *
     * @param weekTypes the list of week types
     */
    private static void addTestWeeks(List<String> weekTypes) {
        for (int i = 0; i < 2; i++) {
            weekTypes.add("Test");
        }
    }

    /**
     * Adds the filler weeks to the week types list.
     *
     * @param weekTypes    the list of week types
     * @param fillerWeeks  the number of filler weeks
     */
    private static void addFillerWeeks(List<String> weekTypes, int fillerWeeks) {
        for (int i = 0; i < fillerWeeks; i++) {
            weekTypes.add("Filler");
        }
    }

    /**
     * Adds additional main block weeks based on the total weeks to the week types list.        
     *
     * @param weekTypes    the list of week types
     * @param totalWeeks   the total number of weeks
     */
    private static void addAdditionalWeeks(List<String> weekTypes, long totalWeeks) {
        int check = determineCheck(totalWeeks);

        if (check == 2) {
            weekTypes.add("Build 2");
            weekTypes.add("Key");
        } else if (check == 3) {
            weekTypes.add("Build 1");
            weekTypes.add("Build 2");
            weekTypes.add("Key");
        }
    }

    /**
     * Determines the need additional main block weeks value based on the total weeks.
     *
     * @param totalWeeks  the total number of weeks
     * @return the check value
     */
    private static int determineCheck(long totalWeeks) {
        return (totalWeeks % 8 == 2 || totalWeeks % 8 == 6) ? 2 : ((totalWeeks % 8 == 3 || totalWeeks % 8 == 7) ? 3 : 0);
    }

    /**
     * Adds main block weeks to the week types list based on the main block cycle.
     *
     * @param weekTypes       the list of week types
     * @param mainBlockCycle  the number of main block cycles
     */
    private static void addMainBlockWeeks(List<String> weekTypes, int mainBlockCycle) {
        String[] mainBlock = { "Recovery", "Build 1", "Build 2", "Key" };

        for (int i = 0; i < mainBlockCycle; i++) {
            for (String weekType : mainBlock) {
                weekTypes.add(weekType);
            }
        }
    }

    /**
     * Generates the plan based on the start date and week types.
     *
     * @param startDate   the start date of the plan
     * @param weekTypes   the list of week types
     * @return the generated plan
     */
    private static List<String> generatePlan(LocalDate startDate, List<String> weekTypes) {
        List<String> plan = new ArrayList<>();
        LocalDate current = startDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM");

        for (int i = 0; i < weekTypes.size(); i++) {
            String weekType = weekTypes.get(i);
            String weekStart = current.format(formatter);
            String weekEnd = current.plusDays(6).format(formatter);
            String weekEntry = String.format("Week #%d - %s - from %s to %s", i + 1, weekType, weekStart, weekEnd);
            plan.add(weekEntry);
            current = current.plusWeeks(1);
        }

        return plan;
    }

    /**
     * Prints the training plan.
     *
     * @param trainingPlan the training plan to be printed
     */
    private static void printTrainingPlan(List<String> trainingPlan) {
        for (String week : trainingPlan) {
            System.out.println(week);
        }
    }

    /**
     * Creates a training plan with an error message.
     *
     * @param errorMessage the error message
     * @return the error training plan
     */
    private static List<String> createErrorPlan(String errorMessage) {
        List<String> errorPlan = new ArrayList<>();
        errorPlan.add(errorMessage);
        return errorPlan;
    }
}
