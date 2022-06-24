package be.desorted.functional.visitor;

import be.desorted.functional.visitor.model.Body;
import be.desorted.functional.visitor.model.Car;
import be.desorted.functional.visitor.model.Engine;
import be.desorted.functional.visitor.model.Wheel;

import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        Car aCar = new Car();

        VisitorInitializer<String> visitorInitializer =
                Visitor.<Car, String>forType(Car.class).execute((Car car) -> "Visited car" + car)
                        .forType(Body.class).execute((Body body) -> "Visited body" + body)
                        .forType(Engine.class).execute((Engine engine) -> "Visited engine" + engine)
                        .forType(Wheel.class).execute((Wheel wheel) -> "Visited wheel" + wheel);

        Visitor<String> visitor = Visitor.of(visitorInitializer);

        String carResult = visitor.visit(aCar);
        System.out.println("car = " + carResult);

        String bodyResult = visitor.visit(aCar.getBody());
        System.out.println("body = " + bodyResult);

        @SuppressWarnings("all")
        VisitableFactory<Car> visitableFactory = VisitableFactory.visiting(Car.class)
                .collectsFrom(
                        car -> car,
                        car -> car.getBody(),
                        car -> car.getEngine(),
                        car -> car.getWheels()[0]
                );

        Visitable<Car> visitableCar = visitableFactory.makeVisitable(aCar);
        List<String> result = visitableCar.accept(visitor, Collectors.toList());
        result.forEach(System.out::println);
    }

}
