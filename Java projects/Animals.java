package Assignment4;

import java.util.Scanner;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *The main class of the program. Its methods are the interface of whole program and
 * are closely connected to the beginning of the execution.
 * Constants contained here are values related to input validation and messages printed when exception occurs.
 */
public class Animals {
    //Exception Messages
    public static final String WEIGHT = "The weight is out of bounds";
    public static final String ENERGY = "The energy is out of bounds";
    public static final String SPEED = "The speed is out of bounds";
    public static final String GRASS = "The grass is out of bounds";
    public static final String INV_NUM_PARAM = "Invalid number of animal parameters";
    public static final String INV_INPUTS = "Invalid inputs";
    public static final String SELF_HUNT = "Self-hunting is not allowed";
    public static final String CANNIBAL = "Cannibalism is not allowed";
    public static final String STRONG_PREY = "The prey is too strong or too fast to attack";
    public static final int MIN_DAYS = 1;
    public static final int MAX_DAYS = 30;
    public static final int MAX_ANIMAL_NUM = 20;
    public static final int TYPE_PARAM = 0;
    public static final int WEIGHT_PARAM = 1;
    public static final int SPEED_PARAM = 2;
    public static final int ENERGY_PARAM = 3;
    public static final int VALID_NUM_OF_PARAMS = 4;
    /*I create instances of ScannerGiver and Field classes as Main class attributes
    to make them available for all methods in Main.*/
    private static ScannerGiver scanGive = new ScannerGiver();
    private static Field field;

    /**
     * Main method is a start point of program execution. All other methods are called from here.
     * Also, here I catch unchecked exceptions.
     * @param args default main parameter.
     */
    public static void main(String[] args) {
        try (Scanner scan = new Scanner(new File("input.txt"))) {
            int days = Integer.parseInt(scan.nextLine());
            if (days < MIN_DAYS | days > MAX_DAYS) {
                throw new InvalidInputsException();
            }
            float grass = Float.parseFloat(scan.nextLine());
            field = new Field(grass);
            scanGive.setScanner(scan);
            List<Animal> animals = readAnimals();
            runSimulation(days, grass, animals);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (GrassOutOfBoundsException e) {
            System.out.println(e.getMessage());
        } catch (InvalidNumberOfAnimalParametersException e) {
            System.out.println(e.getMessage());
        } catch (InvalidInputsException e) {
            System.out.println(e.getMessage());
        } catch (WeightOutOfBoundsException e) {
            System.out.println(e.getMessage());
        } catch (SpeedOutOfBoundsException e) {
            System.out.println(e.getMessage());
        } catch (EnergyOutOfBoundsException e) {
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println(INV_INPUTS);
        }
    }


    /**
     * This method reads animals from the input file one by one.
     * It uses Scanner-object taken from main method through scanGive.
     * @return list of animals created with inputted parameters.
     * @throws InvalidInputsException is thrown when number of animals is invalid,
     * or actual number of animals does not coincide with declared number, or animal parameters have wrong format.
     * @throws InvalidNumberOfAnimalParametersException is thrown when number of animal parameters is invalid.
     */
    private static List<Animal> readAnimals() throws InvalidInputsException, InvalidNumberOfAnimalParametersException {
        Scanner scan = scanGive.getScanner();
        int animalNum = Integer.parseInt(scan.nextLine());
        if (animalNum < 1 | animalNum > MAX_ANIMAL_NUM) {
            throw new InvalidInputsException();
        }
        String[] animParams;
        List<Animal> animals = new ArrayList<>();
        for (int i = 0; i < animalNum; ++i) {
            animParams = scan.nextLine().split(" ");
            if (animParams.length != VALID_NUM_OF_PARAMS) {
                throw new InvalidNumberOfAnimalParametersException();
            }
            animals.add(createAnimal(animParams));
            if (i != (animalNum - 1) & !scan.hasNextLine()) {
                throw new InvalidInputsException();
            }
            if (i == (animalNum - 1) & scan.hasNextLine()) {
                throw new InvalidInputsException();
            }
        }
        return animals;
    }

    /**
     * This method starts a simulation according to rules from the task.
     * Here, in the second for-loop, I catch checked exceptions.
     * @param days number of simulated days.
     * @param grassAmount initial amount of grass on the field.
     * @param animals list of animals living on the field.
     */
    private static void runSimulation(int days, float grassAmount, List<Animal> animals) {
        removeDeadAnimals(animals);
        for (int i = 0; i < days; ++i) {
            int animNum = animals.size();
            for (int j = 0; j < animNum; ++j) {
                try {
                    animals.get(j).eat(animals, field);
                } catch (SelfHuntingException e) {
                    System.out.println(e.getMessage());
                } catch (CannibalismException e) {
                    System.out.println(e.getMessage());
                } catch (TooStrongPreyException e) {
                    System.out.println(e.getMessage());
                }
            }
            field.makeGrassGrow();
            for (int j = 0; j < animNum; ++j) {
                animals.get(j).decrementEnergy();
            }
            removeDeadAnimals(animals);
        }
        printAnimals(animals);
    }

    /**
     * This method makes each animal survived after simulation make a sound.
     * @param animals list of animals left on the field.
     */
    private static void printAnimals(List<Animal> animals) {
        int survived = animals.size();
        for (int i = 0; i < survived; ++i) {
            animals.get(i).makeSound();
        }
    }

    /**
     * This method removes dead animals form the list.
     * @param animals list of animals, from which we should remove dead ones.
     */
    private static void removeDeadAnimals(List<Animal> animals) {
        int size = animals.size();
        for (int k = 0; k < size; k++) {
            if (animals.get(k).isDead()) {
                animals.remove(k);
                --size;
                --k;
            }
        }
    }

    /**
     * This method create an Animal-objects with inputted parameters.
     * @param params array of strings that represent animal parameters.
     * @return created Animal-object
     */
    private static Animal createAnimal(String[] params) {
        float weight = Float.parseFloat(params[WEIGHT_PARAM]);
        float speed = Float.parseFloat(params[SPEED_PARAM]);
        float energy = Float.parseFloat(params[ENERGY_PARAM]);
        switch (params[TYPE_PARAM]) {
            case "Lion":
                return new Lion(weight, speed, energy, AnimalSound.LION);
            case "Zebra":
                return new Zebra(weight, speed, energy, AnimalSound.ZEBRA);
            case "Boar":
                return new Boar(weight, speed, energy, AnimalSound.BOAR);
            default:
                throw new InvalidInputsException();
        }
    }
}

//EXCEPTIONS
/**
 * This is exception thrown, when inputted weight of animal has invalid value.
 */
class WeightOutOfBoundsException extends RuntimeException {
    /**
     * Method used to get error message for this exception.
     * @return invalid weight message.
     */
    public String getMessage() {
        return Main.WEIGHT;
    }
}

/**
 * This is exception thrown, when inputted energy of animal has invalid value.
 */
class EnergyOutOfBoundsException extends RuntimeException {
    /**
     * Method used to get error message for this exception.
     * @return invalid energy message.
     */
    public String getMessage() {
        return Main.ENERGY;
    }
}

/**
 * This is exception thrown, when inputted speed of animal has invalid value.
 */
class SpeedOutOfBoundsException extends RuntimeException {
    /**
     * Method used to get error message for this exception.
     * @return invalid speed message.
     */
    public String getMessage() {
        return Main.SPEED;
    }
}

/**
 * This is exception thrown, when inputted amount of grass has invalid value.
 */
class GrassOutOfBoundsException extends RuntimeException {
    /**
     * Method used to get error message for this exception.
     * @return invalid grass amount message.
     */
    public String getMessage() {
        return Main.GRASS;
    }
}

/**
 * This is exception thrown, when number of inputted animal parameters is invalid.
 */
class InvalidNumberOfAnimalParametersException extends RuntimeException {
    /**
     * Method used to get error message for this exception.
     * @return invalid number of animal parameters message.
     */
    public String getMessage() {
        return Main.INV_NUM_PARAM;
    }
}

/**
 * This is exception thrown, when values of inputted number of days and number of animals are invalid,
 * or actual number of inputted animals does not correspond to the declared one,
 * or inputted animal parameters have invalid format.
 */
class InvalidInputsException extends RuntimeException {
    /**
     * Method used to get error message for this exception.
     * @return Invalid inputs message.
     */
    public String getMessage() {
        return Main.INV_INPUTS;
    }
}

/**
 * This is exception thrown, when an animal tries to hunt itself.
 */
class SelfHuntingException extends Exception {
    /**
     * Method used to get error message for this exception.
     * @return self-hunting message.
     */
    public String getMessage() {
        return Main.SELF_HUNT;
    }
}

/**
 * This is exception thrown when a hunter tries to hunt a prey that is not weaker and not slower than hunter.
 */
class TooStrongPreyException extends Exception {
    /**
     * Method used to get error message for this exception.
     * @return too strong or too fast prey message.
     */
    public String getMessage() {
        return Main.STRONG_PREY;
    }
}

/**
 * This is exception thrown, when a hunter tries to hunt an animal of its kind.
 */
class CannibalismException extends Exception {
    /**
     * Method used to get error message for this exception.
     * @return cannibalism message.
     */
    public String getMessage() {
        return Main.CANNIBAL;
    }
}


//ENUMERATIONS
/**
 * Enumeration that represents sounds made by animals.
 */
enum AnimalSound {
    LION("Roar"),
    ZEBRA("Ihoho"),
    BOAR("Oink");
    private final String sound;

    /**
     * Constructor for AnimalSound enum.
     * @param sound sound made by an animal.
     */
    AnimalSound(String sound) {
        this.sound = sound;
    }

    /**
     * Method used to get sounds of animals.
     * @return sound of this AnimalSound-object.
     */
    public String getSound() {
        return sound;
    }
}


//INTERFACES
/**
 * Interface that represents and implements actions that all carnivores can do.
 * Constants here help to operate with list of animals.
 * @param <T> the exact class of hunter.
 */
interface Carnivore<T extends Animal> {
    int ONE_STEP = 1;
    int FST_IND = 0;
    /**
     * In this method, the potential prey for hunter is chosen.
     * @param animals list of animals on the field.
     * @param hunter the animal that is going to hunt.
     * @return animal chosen as prey.
     * @throws SelfHuntingException exception thrown, when the chosen prey is hunter itself.
     * @throws CannibalismException exception thrown, when the chosen prey is of hunter's kind.
     */
    default Animal choosePrey(List<Animal> animals, T hunter) throws SelfHuntingException, CannibalismException {
        int hunterInd = animals.indexOf(hunter);
        int preyInd = hunterInd + ONE_STEP;
        if (preyInd == animals.size()) {
            preyInd = FST_IND;
        }
        if (animals.size() == 1) {
            throw new SelfHuntingException();
        }
        Class<?> hunterClass = hunter.getClass();
        if (animals.get(preyInd).getClass().equals(hunterClass)) {
            throw new CannibalismException();
        }
        return animals.get(preyInd);
    }

    /**
     * In this method, I check the ability of hunting and perform a hunt
     * (hunter's energy incremented by value of prey's weight, prey's energy is set to 0).
     * @param hunter the animal that is going to hunt.
     * @param prey the animal chosen as prey.
     * @throws TooStrongPreyException exception thrown, when hunter's energy and speed are lower than prey's ones.
     */
    default void huntPrey(Animal hunter, Animal prey) throws TooStrongPreyException {
        float hunterEnergy = hunter.getEnergy();
        float preyEnergy = prey.getEnergy();
        float hunterSpeed = hunter.getSpeed();
        float preySpeed = prey.getSpeed();
        if (hunterEnergy <= preyEnergy & hunterSpeed <= preySpeed) {
            throw new TooStrongPreyException();
        } else {
            hunterEnergy += prey.getWeight();
            hunter.setEnergy(hunterEnergy);
            prey.setEnergy(Animal.MIN_ENERGY);
        }
    }
}

/**
 * Interface that represents and implements actions that all herbivores can do.
 * Constant GRAZ_FRACT represents fraction 1/10.
 */
interface Herbivore {
    float GRAZ_FRACT = 0.1f;

    /**
     * In this method, I check the ability to graze
     * (grass amount of the field is greater than 1/10 of grazer's weight) and perform grazing
     * (grazer's energy incremented by value of 1/10 of its weight, grass amount is decremented similarly).
     * @param grazer the animal that is going to graze.
     * @param field the field that is going to be used by grazer.
     */
    default void grazeInTheField(Animal grazer, Field field) {
        float grazerEnergy = grazer.getEnergy();
        float grassAmount = field.getGrassAmount();
        float grazerWeight = grazer.getWeight();
        if (grassAmount < grazerWeight * GRAZ_FRACT) {
            return;
        } else {
            grazer.setEnergy(grazerEnergy +  grazerWeight * GRAZ_FRACT);
            field.setGrassAmount(grassAmount - grazerWeight * GRAZ_FRACT);
        }
    }
}

/**
 * Interface that represents actions that all omnivores can do (both carnivore and herbivore actions).
 * @param <T> the exact type of omnivore.
 */
interface Omnivore<T extends Animal> extends Carnivore<T>, Herbivore { }


//CLASSES
/**
 * Class that contains attributes and methods common for all animals.
 * Constants in this class connected with parameter value validation.
 */
abstract class Animal {
    public static final float MIN_SPEED = 5.0f;
    public static final float MAX_SPEED = 60.0f;
    public static final float MIN_ENERGY = 0.0f;
    public static final float MAX_ENERGY = 100.0f;
    public static final float MIN_WEIGHT = 5.0f;
    public static final float MAX_WEIGHT = 200.0f;
    private float weight;
    private float speed;
    private float energy;
    private AnimalSound sound;

    /**
     * Constructor for Animal class.
     * Here exceptions connected to invalid values of animal parameters are thrown.
     * @param weight weight of animal.
     * @param speed speed of animal.
     * @param energy energy of animal.
     */
    protected Animal(float weight, float speed, float energy) {
        this.weight = weight;
        this.speed = speed;
        this.energy = energy;
        if (MIN_WEIGHT > weight | weight > MAX_WEIGHT) {
            throw new WeightOutOfBoundsException();
        }
        if (MIN_ENERGY > energy | energy > MAX_ENERGY) {
            throw new EnergyOutOfBoundsException();
        }
        if (MIN_SPEED > speed | speed > MAX_SPEED) {
            throw new SpeedOutOfBoundsException();
        }
    }

    /**
     * This method decrements energy of an animal by 1 (happens each time at the end of simulation day).
     */
    public void decrementEnergy() {
        this.setEnergy(this.energy - 1f);
    }

    /**
     * This is an abstract method that represents ability to eat, common for all animals.
     * @param animals list of animals living in the field.
     * @param field field, on which these animals live.
     * @throws CannibalismException thrown, when an animal tries to eat animal of the same kind.
     * @throws SelfHuntingException thrown, when an animal tries to hunt itself.
     * @throws TooStrongPreyException thrown, when an animal tries to hunt a prey,
     * which energy and speed are not lower than hunter's ones.
     */
    public abstract void eat(List<Animal> animals, Field field)
            throws CannibalismException, SelfHuntingException, TooStrongPreyException;

    /**
     * This method prints a sound corresponding to an animal.
     */
    public void makeSound() {
        System.out.println(sound.getSound());
    }

    /**
     * Setter for sound.
     * @param sound sound to be set.
     */
    public void setSound(AnimalSound sound) {
        this.sound = sound;
    }

    /**
     * Getter for energy.
     * @return current energy of an animal.
     */
    public float getEnergy() {
        return energy;
    }

    /**
     * Getter for speed.
     * @return speed of an animal.
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Getter for weight.
     * @return weight of an animal.
     */
    public float getWeight() {
        return weight;
    }

    /**
     * Setter for energy.
     * @param energy energy to be set.
     */
    public void setEnergy(float energy) {
        if (energy > MAX_ENERGY) {
            this.energy = MAX_ENERGY;
        } else {
            this.energy = energy;
        }
    }

    /**
     * This method checks, if an animal is dead (energy <= 0).
     * @return true, if an animal is dead; false, if not.
     */
    public boolean isDead() {
        return this.energy <= 0f;
    }
}

/**
 * This class implements methods common to all Zebras.
 * It implements interface Herbivore.
 */
class Zebra extends Animal implements Herbivore {

    /**
     * Constructor for Zebra class.
     * @param weight weight of a zebra.
     * @param speed speed of a zebra.
     * @param energy energy of a zebra.
     */
    public Zebra(float weight,  float speed, float energy) {
        super(weight, speed, energy);
    }

    /**
     * Overloaded constructor for Zebra class (sound added).
     * @param weight weight of a zebra.
     * @param speed speed of a zebra.
     * @param energy energy of a zebra.
     * @param sound sound corresponding to zebra.
     */
    public Zebra(float weight,  float speed, float energy, AnimalSound sound) {
        super(weight, speed, energy);
        this.setSound(sound);
    }

    /**
     * Exact implementation of eating for zebras (grazing).
     * @param animals list of animals living in the field.
     * @param field   field, on which these animals live.
     */
    @Override
    public void eat(List<Animal> animals, Field field) {
        if (this.isDead()) {
            return;
        }
        this.grazeInTheField(this, field);
    }
}

/**
 * This class implements methods common for all lions.
 * It implements interface Carnivore.
 */
class Lion extends Animal implements Carnivore {

    /**
     * Constructor for class Lion.
     * @param weight weight of a lion.
     * @param speed speed of a lion.
     * @param energy energy of a lion.
     */
    public Lion(float weight,  float speed, float energy) {
        super(weight, speed, energy);
    }

    /**
     * Overloaded constructor for class Lion (sound added).
     * @param weight weight of a lion.
     * @param speed speed of a lion.
     * @param energy energy of a lion.
     * @param sound sound corresponding to lion.
     */
    public Lion(float weight,  float speed, float energy, AnimalSound sound) {
        super(weight, speed, energy);
        this.setSound(sound);
    }

    /**
     * Exact implementation of eating for lions (hunting).
     * @param animals list of animals living in the field.
     * @param field   field, on which these animals live.
     * @throws CannibalismException thrown, when a lion tries to eat another lion.
     * @throws SelfHuntingException thrown, when a lion tries to hunt itself.
     * @throws TooStrongPreyException thrown, when a lion tries to hunt a prey,
     * which energy and speed are not lower than hunter's ones.
     */
    @Override
    public void eat(List<Animal> animals, Field field)
            throws CannibalismException, SelfHuntingException, TooStrongPreyException {
        if (this.isDead()) {
            return;
        }
        Animal prey = this.choosePrey(animals, this);
        this.huntPrey(this, prey);
    }
}

/**
 * This class implements methods common for all boars.
 * It implements interface Omnivore.
 */
class Boar extends Animal implements Omnivore {
    /**
     * Constructor for class Boar.
     * @param weight weight of a boar.
     * @param speed speed of a boar.
     * @param energy energy of a boar.
     */
    public Boar(float weight,  float speed, float energy) {
        super(weight, speed, energy);
    }

    /**
     * Overloaded constructor for class Boar (sound added).
     * @param weight weight of a boar.
     * @param speed speed of a boar.
     * @param energy energy of a boar.
     * @param sound sound corresponding to a boar.
     */
    public Boar(float weight,  float speed, float energy, AnimalSound sound) {
        super(weight, speed, energy);
        this.setSound(sound);
    }

    /**
     * Exact implementation of eating for boars (grazing and then hunting).
     * @param animals list of animals living in the field.
     * @param field   field, on which these animals live.
     * @throws CannibalismException thrown, when a boar tries to eat another boar.
     * @throws SelfHuntingException thrown, when a boar tries to hunt itself.
     * @throws TooStrongPreyException thrown, when a boar tries to hunt a prey,
     * which energy and speed are not lower than hunter's ones.
     */
    public void eat(List<Animal> animals, Field field)
            throws CannibalismException, SelfHuntingException, TooStrongPreyException {
        if (this.isDead()) {
            return;
        }
        this.grazeInTheField(this, field);
        Animal prey = this.choosePrey(animals, this);
        this.huntPrey(this, prey);
    }
}

/**
 * Class that contains attributes and implements methods common for all fields.
 * Constants here are connected to field parameters validation or help to implement grass growing.
 */
class Field {
    private static final float MIN_GRASS_AMOUNT = 0.0f;
    private static final float MAX_GRASS_AMOUNT = 100.0f;
    private static final float INCREASE_STEP = 2.0f;

    private float grassAmount;

    /**
     * Constructor for class Field.
     * Here exception connected to invalid value of grass amount is thrown.
     * @param grassAmount amount of grass in the field.
     */
    public Field(float grassAmount) {
        this.grassAmount = grassAmount;
        if (MIN_GRASS_AMOUNT > grassAmount | grassAmount > MAX_GRASS_AMOUNT) {
            throw new GrassOutOfBoundsException();
        }
    }

    /**
     * This method implements growing of grass in the field (doubles each time at the end of the day).
     */
    public void makeGrassGrow() {
        if (INCREASE_STEP * grassAmount > MAX_GRASS_AMOUNT) {
            grassAmount = MAX_GRASS_AMOUNT;
        } else {
            grassAmount *= INCREASE_STEP;
        }
    }

    /**
     * Getter for grass amount.
     * @return current grass amount.
     */
    public float getGrassAmount() {
        return this.grassAmount;
    }

    /**
     * Setter for grass amount.
     * @param grassAmount grass amount to be set.
     */
    public void setGrassAmount(float grassAmount) {
        if (grassAmount > MAX_GRASS_AMOUNT) {
            this.grassAmount = MAX_GRASS_AMOUNT;
        } else {
            this.grassAmount = grassAmount;
        }
    }
}

/**
 * This class is needed to pass the same instance of Scanner through different methods of Main class.
 * It is necessary because we must save the progress of reading from the file when calling for another reading method.
 */
class ScannerGiver {
    private Scanner scan;

    /**
     * Constructor for class ScannerGiver.
     */
    public ScannerGiver() { }

    /**
     * Setter for scan.
     * @param scan instance of Scanner class.
     */
    public void setScanner(Scanner scan) {
        this.scan = scan;
    }

    /**
     * Getter for scan.
     * @return instance of scanner class.
     */
    public Scanner getScanner() {
        return this.scan;
    }
}
