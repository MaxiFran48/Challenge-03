package com.literalura.challenge03;

import com.literalura.challenge03.controlador.LiteraluraControlador;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class Challeng03Application {

    public static void main(String[] args) {

        boolean salir = false;
        Scanner input = new Scanner(System.in);
        ApplicationContext applicationContext = SpringApplication.run(Challeng03Application.class, args);
        LiteraluraControlador controlador = applicationContext.getBean(LiteraluraControlador.class);

        System.out.println("Bienvenido a Literaulera! Su libreria de confianza\n");

        do {

            System.out.print("""
                Ingrese el numero de accion que desea realizar: 
                1) Buscar libros por titulo
                2) Listar libros registrados
                3) Listar autores registrados
                4) Listar autores vivos en un año dado
                5) Listar libros por idioma
                6) Salir
                
                Numero: """);

            int opcionElegida = 6;
            boolean ingresoInvalido;

            do {
                ingresoInvalido = false;
                try {
                    opcionElegida = Integer.parseInt(input.nextLine().trim());

                    if (opcionElegida < 1 || opcionElegida > 6) {
                        throw new IllegalArgumentException();
                    }
                } catch (Exception e) {
                    System.out.print("\nLa respuesta ingresada es invalida, intente nuevamente: ");
                    ingresoInvalido = true;
                }
            } while (ingresoInvalido);



            switch (opcionElegida) {
                case 1: {
                    controlador.buscarLibroPorTitulo();
                    break;
                }

                case 2: {
                    controlador.listarLibrosRegistrados();
                    break;
                }

                case 3: {
                    controlador.listarAutoresRegistrados();
                    break;
                }

                case 4: {
                    controlador.listarAutoresVivosPorAño();
                    break;
                }

                case 5: {
                    controlador.listarLibrosPorIdioma();
                    break;
                }

                case 6: {
                    salir = true;
                }
            }

        } while (!salir);

        System.out.println("Aplicación finalizada, gracias por su visita!");

    }

}
