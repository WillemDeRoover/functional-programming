package be.desorted.functional.visitor.model;


import be.desorted.functional.visitor.Visitor;

import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class Car {

    private final Body body = new Body();
    private final Engine engine = new Engine();
    private final Wheel[] wheels = {new Wheel(), new Wheel(), new Wheel(), new Wheel()};

    @Override
    public String toString() {
        return "Car{" +
                "body=" + body +
                ", engine=" + engine +
                ", wheels=" + Arrays.toString(wheels) +
                '}';
    }

    public Body getBody() {
        return body;
    }

    public Engine getEngine() {
        return engine;
    }

    public Wheel[] getWheels() {
        return wheels;
    }
}
