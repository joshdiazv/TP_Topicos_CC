import java.util.Scanner;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.Solver;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese la cantidad de países:");
        int numPaises = scanner.nextInt();

        Model model = new Model("Simulador de Propagación de Enfermedades");

        IntVar[] infectados = new IntVar[numPaises];
        int[] poblacion = new int[numPaises];
        int[] recursosMedicos = new int[numPaises];

        for (int i = 0; i < numPaises; i++) {
            System.out.println("Ingrese la población y los recursos médicos para el país " + (i + 1) + ":");
            poblacion[i] = scanner.nextInt();
            recursosMedicos[i] = scanner.nextInt();
            if (i == 0) {
                // Empezar con más infectados en el primer país para observar la propagación
                infectados[i] = model.intVar("Infectados_Pais" + (i + 1), 50, Math.min(poblacion[i], recursosMedicos[i]));
            } else {
                infectados[i] = model.intVar("Infectados_Pais" + (i + 1), 1, Math.min(poblacion[i], recursosMedicos[i]));
            }
        }

        System.out.println("Ingrese la tasa de contagio en porcentaje (sin el signo %):");
        int tasaContagio = scanner.nextInt();

        // Aplicando restricciones
        for (int i = 0; i < numPaises - 1; i++) {
            IntVar propagacion = model.intVar("Propagacion_Pais" + (i + 1), 0, poblacion[i+1]);
            model.times(infectados[i], tasaContagio, propagacion).post();
            IntVar adjustedPropagation = propagacion.div(100).intVar();
            model.arithm(infectados[i + 1], ">=", adjustedPropagation).post();
        }

        // Resolver el modelo
        Solver solver = model.getSolver();
        if (solver.solve()) {
            for (int i = 0; i < numPaises; i++) {
                System.out.println("País " + (i + 1) + ": Infectados = " + infectados[i].getValue());
            }
        } else {
            System.out.println("No se encontró solución.");
        }
        scanner.close();
    }
}