#include <iostream>
#include <vector>
#include <bits/stdc++.h>
#include <string>


/*Abstract class Animal, representing features common for all animals.*/
class Animal {
    private:
        const std::string name;
        int daysLived;
        bool dead;

    public:
        Animal() {}

        Animal(const std::string name, int daysLived) : name(name), daysLived(daysLived), dead(false) {}

        Animal(Animal& other) : name(other.getName()), daysLived(other.getDaysLived()), dead(other.isDead()) {}

        ~Animal() = default;

        int getDaysLived() { return this->daysLived; }

        std::string getName() { return this->name; }

        bool isDead() { return this->dead; }

        void sayName() {
            std::cout << "My name is " << this->name <<
                ", days lived: " << this->daysLived << std::endl;
        }

        virtual void attack(Animal& other) = 0;

        void setDaysLived(int newValue) {
            this->daysLived = newValue;
        }
}; 

/*Class Fish, derived from Animal. Virtual inheritance resolves dimond problem for Monster.*/
class Fish : public virtual Animal {
    public:
        Fish(const std::string name, int daysLived) : Animal(name, daysLived) {}

        void attack(Animal& other) {
            std::cout << "Fish is attacking" << std::endl;
        }
};

/*Class Bird, derived from Animal. Virtual inheritance resolves dimond problem for Monster.*/
class Bird : public virtual Animal {
    public:
        Bird(std::string name, int daysLived) : Animal(name, daysLived) {}

        void attack(Animal& other) {
            std::cout << "Bird is attacking" << std::endl;
        }
};

/*Class Mouse, derived from Animal. Virtual inheritance resolves dimond problem for Monster.*/
class Mouse : public virtual Animal {
    public:
        Mouse(std::string name, int daysLived) : Animal(name, daysLived) {}

        void attack(Animal& other) {
            std::cout << "Mouse is attacking" << std::endl;
        }
};

/*Class BetterFish, derived from Fish. Represents a fish after applying substance.*/
class BetterFish : public Fish {
    public:
        BetterFish(std::string name, int daysLived) : Fish(name, daysLived), Animal(name, daysLived) {}

        BetterFish(Fish& fish) : Fish(fish), Animal(fish) {}
        
        void attack(Animal& other) override {
            std::cout << "BetterFish is attacking" << std::endl;
        }
};

/*Class BetterBird, derived from Bird. Represents a bird after applying substance.*/
class BetterBird : public Bird {
    public:
        BetterBird(std::string name, int daysLived) : Bird(name, daysLived), Animal(name, daysLived) {}

        BetterBird(Bird& bird) : Bird(bird), Animal(bird) {}

        void attack(Animal& other) {
            std::cout << "BetterBird is attacking" << std::endl;
        }
};

/*Class BetterMouse, derived from Mouse. Represents a mouse after applying substance.*/
class BetterMouse : public Mouse {
    public:
        BetterMouse(std::string name, int daysLived) : Mouse(name, daysLived), Animal(name, daysLived) {}

        BetterMouse(Mouse& mouse) : Mouse(mouse), Animal(mouse) {}

        void attack(Animal& other) {
            std::cout << "BetterMouse is attacking" << std::endl;
        }
};

/*Class Monster, derived from all "Better" classes. Represents an animal after applying substance twice.*/
class Monster : public BetterFish, BetterBird, BetterMouse {
    public:
        Monster(std::string name) : BetterFish(name, 1), BetterBird(name, 1), BetterMouse(name, 1), Animal(name, 1) {}

        Monster(Animal& animal) : BetterFish(animal.getName(), 1), BetterBird(animal.getName(), 1), BetterMouse(animal.getName(), 1), Animal(animal.getName(), 1) {}

        void attack(Animal& other) {}
};

/*Lambda-comparator for Animal and all derived classes, used in container sorting.*/
auto comparator = [](Animal* a1, Animal* a2) {
    if (a1->getDaysLived() != a2->getDaysLived()) {
        return a1->getDaysLived() < a2->getDaysLived();
    } else {
        return a1->getName() < a2->getName();
    }
};

/*Base container class template, providing common container interface. I use vector as a strorage for container's elements.*/
template <typename T>
class Container {
    private:
        std::vector<T> container;
    public:
        Container() {}

        /*After adding an element I sort vector with elements using comparator.*/
        void addElement(T elem) {
            container.push_back(elem);
            sort(container.begin(), container.end(), comparator);
        }

        /*Function for access to container's elements.*/
        T getElement(int index) {
            if (index >= container.size()) {
                return nullptr;
            } else {
                return container[index];
            }
        }

        int size() {
            return container.size();
        }

        /*Function removes element by index and sorts the container.*/
        void removeElement(int index) {
            container.erase(container.begin() + index);
            sort(container.begin(), container.end(), comparator);
        }

        /*Function that removes all dead animals (daysLived > 10) from the container and prints death meassage for each one.*/
        virtual void removeDead() {
            for (int i = 0; i < this->size(); i++) {
                T obj = this->getElement(i);
                if (obj->getDaysLived() > 10) {
                    this->removeElement(i);
                    std::cout << obj->getName() << " has died of old days" << std::endl;
                    --i;
                }
            }
        }

        /*Function that increments daysLives for all animals in container.*/
        void incrementDaysForAll() {
            for (int i = 0; i < this->size(); i++) {
                T obj = this->getElement(i);
                obj->setDaysLived(obj->getDaysLived()+1);
            }    
        }

        /*Function that removes all animals from the container.*/
        void killAll() {
            this->container.clear();
        }

        /*Begin and end functions for iterating through container.*/
        auto begin() {
            return container.begin();
        }

        auto end() {
            return container.end();
        }
};

/*Cage class template, derived from container of pointers to Animal.*/
template <typename T>
class Cage : public Container<Animal*> {
    public:
        Cage() : Container<Animal*>() {}
};

/*Cage template specialization to prevent creation of Cages for fish.*/
template<>
class Cage<Fish> : public Container<Fish> {
    public:
        Cage() = delete;
};

/*Aquarium class template, derived from container of pointers to Animal.*/
template <typename T>
class Aquarium : public Container<Animal*> {
    public:
        Aquarium() : Container<Animal*>() {}
};

/*Aquarium template specialization to prevent creation of Aquariums for birds.*/
template<>
class Aquarium<Bird> : public Container<Bird> {
    public:
        Aquarium() = delete;
};

/*Class template Freedom is for animals that were born in freedom and for monsters.*/
template <typename T>
class Freedom : public Container<T> {
    public:
        Freedom() : Container<T>() {}
        
        /*Frredom rquires specific realization of removeDead, as monsters die on the second day (daysLived > 1).*/
        void removeDead() override {
            for (int i = 0; i < this->size(); i++) {
                T obj = this->getElement(i);
                Monster* m = dynamic_cast<Monster*>(obj);
                if (m) {
                    if (obj->getDaysLived() > 1) {
                        this->removeElement(i);
                        std::cout << m->getName() << " has died of old days" << std::endl;
                        --i;
                    }
                }
                if (obj->getDaysLived() > 10) {
                    this->removeElement(i);
                    std::cout << obj->getName() << " has died of old days" << std::endl;
                    --i;
                }
            }
        }
};

/*CommandManager includes all functions for processing commands.*/
class CommandManager {
    private:
    /*Here instances of all needed containers created in advance.*/
        Cage<Bird*> birdCage;
        Cage<BetterBird*> betterBirdCage;
        Cage<Mouse*> mouseCage;
        Cage<BetterMouse*> betterMouseCage;
        Aquarium<Fish*> fishAq;
        Aquarium<BetterFish*> betterFishAq;
        Aquarium<Mouse*> mouseAq;
        Aquarium<BetterMouse*> betterMouseAq;
        Freedom<Animal*> freedom;
    public:
        CommandManager() {}

        /*Selection function that calls appropriate function based on command name.*/
        void processCommand(std::vector<std::string> command) {
            if (command[0] == "CREATE") {
                create(command);
            } else if (command[0] == "APPLY_SUBSTANCE") {
                applySubstance(command);
            } else if(command[0] == "TALK") {
                talk(command);
            } else if(command[0] == "REMOVE_SUBSTANCE") {
                removeSubstance(command);
            } else if(command[0] == "ATTACK") {
                attack(command);
            } else {
                period();
            }
        }

        /*Function for creating new animals with given parameters in appropriate containers.*/
        void create(std::vector<std::string> command) {
            Animal* animal = createBasedOnCommand(command[1], command[2], command[5]);
            addToRightContainer(animal, command[1], command[4]);
            animal->sayName();
        }

        /*Helper-function that creates new Animal with required parameters.*/
        Animal* createBasedOnCommand(std::string type, std::string name, std::string daysLived) {
            if (type == "M") {
                return new Mouse(name, std::stoi(daysLived));
            } else if (type == "F") {
                return new Fish(name, std::stoi(daysLived));
            } else if (type == "B") {
                return new Bird(name, std::stoi(daysLived));
            } else if (type == "BM") {
                return new BetterMouse(name, std::stoi(daysLived));
            } else if (type == "BF") {
                return new BetterFish(name, std::stoi(daysLived));
            } else if (type == "BB"){
                return new BetterBird(name, std::stoi(daysLived));
            }
        }

        /*Helper-function that take created animal and places into right container.*/
        void addToRightContainer(Animal* animal, std::string type, std::string container) {
            if (type == "M") {
                Mouse* mouse = dynamic_cast<Mouse*>(animal);
                if (container == "Cage") {
                    if (mouse) {
                        this->mouseCage.addElement(mouse);
                    }    
                } else if (container == "Aquarium") {
                    if (mouse) {
                        this->mouseAq.addElement(mouse);
                    }
                } else {
                    this->freedom.addElement(animal);
                }
            } else if (type == "B") {
                if (container == "Cage") {
                    Bird* bird = dynamic_cast<Bird*>(animal);
                    if (bird) {
                        this->birdCage.addElement(bird);
                    }    
                } else {
                    this->freedom.addElement(animal);
                }
            } else if (type == "F"){
                if (container == "Aquarium") {
                    Fish* fish = dynamic_cast<Fish*>(animal);
                    if (fish) {
                        this->fishAq.addElement(fish);
                    }    
                } else {
                    this->freedom.addElement(animal);        
                }
            } else if (type == "BM") {
                BetterMouse* bMouse = dynamic_cast<BetterMouse*>(animal);
                if (container == "Cage") {
                    if (bMouse) {
                        this->betterMouseCage.addElement(bMouse);
                    }    
                } else if (container == "Aquarium") {
                    if (bMouse) {
                        this->betterMouseAq.addElement(bMouse);
                    }
                } else {
                    this->freedom.addElement(animal);
                }    
            } else if (type == "BB") {
                if (container == "Cage") {
                    BetterBird* bBird = dynamic_cast<BetterBird*>(animal);
                    if (bBird) {
                        this->betterBirdCage.addElement(bBird);
                    }    
                } else {
                    this->freedom.addElement(animal);
                }
            } else {
                if (container == "Aquarium") {
                    BetterFish* bFish = dynamic_cast<BetterFish*>(animal);
                    if (bFish) {
                        this->betterFishAq.addElement(bFish);
                    }    
                } else {
                    this->freedom.addElement(animal);
                }
            }
        }

        /*Function for applying substance to the specific animals. It checks if the animal is not in Freedom container (not applicable in this case) 
        and if the required animal is in the container.*/
        void applySubstance(std::vector<std::string> command) {
            if (command[1] == "Freedom") {
                std::cout << "Substance cannot be applied in freedom" << std::endl;
                return;
            }
            Container<Animal*>* container = findContainer(command[1], command[2]);
            if (container) {
                int pos = std::stoi(command[3]);
                if (container->size() <= pos) {
                    notFound();
                    return;
                } else {
                    Animal* animal = container->getElement(pos);
                    container->removeElement(pos);
                    upgradeAnimal(animal, command[2], command[1]);
                }
            }
        }

        /*Helper-function that finds required container based on container type and animal type.*/
        Container<Animal*>* findContainer(std::string container, std::string type) {
            if (container == "Cage") {
                if (type == "M") {
                    return &this->mouseCage;
                } else if (type == "BM") {
                    return &this->betterMouseCage;
                } else if (type == "B") {
                    return &this->birdCage;
                } else {
                   return &this->betterBirdCage;
                }
            } else if (container == "Aquarium") {
                if (type == "M") {
                    return &this->mouseAq;
                } else if (type == "BM") {
                   return &this->betterMouseAq;
                } else if (type == "F") {
                    return &this->fishAq;
                } else {
                    return &this->betterFishAq;
                }
            } else {    
                return &this->freedom;
            }
        }

        /*Helper-function that prints "Not found" message.*/
        void notFound() {
            std::cout << "Animal not found" << std::endl;
        }

        /*Helper-function that upgrades animal. It creates "Better" class based on animal of regular class.
        It creates Moster, if the input animal was of "Better class". After creation it places animal into the right container
        (and kills every animal in the previous container in case of Monster).*/
        void upgradeAnimal(Animal* animal, std::string type, std::string container) {
            if (type == "M") {
                BetterMouse* bm = new BetterMouse(*dynamic_cast<Mouse*>(animal));
                int halvedDays = bm->getDaysLived()%2 == 0 ? bm->getDaysLived()/2 : bm->getDaysLived()/2 + 1;
                bm->setDaysLived(halvedDays);
                if (container == "Cage") {
                    betterMouseCage.addElement(bm);
                } else {
                    betterMouseAq.addElement(bm);
                }
            } else if (type == "B") {
                BetterBird* bb = new BetterBird(*dynamic_cast<Bird*>(animal));
                int halvedDays = bb->getDaysLived()%2 == 0 ? bb->getDaysLived()/2 : bb->getDaysLived()/2 + 1;
                bb->setDaysLived(halvedDays);
                betterBirdCage.addElement(bb);
            } else if (type == "F") {
                BetterFish* bf = new BetterFish(*dynamic_cast<Fish*>(animal));
                int halvedDays = bf->getDaysLived()%2 == 0 ? bf->getDaysLived()/2 : bf->getDaysLived()/2 + 1;
                bf->setDaysLived(halvedDays);
                betterFishAq.addElement(bf);
            } else if (type == "BM") {
                Monster* m = new Monster(*animal);
                if (container == "Cage") {
                   betterMouseCage.killAll();
                } else {
                    betterMouseAq.killAll();
                }
                freedom.addElement(m);
            } else if (type == "BB") {
                Monster* m = new Monster(*animal);
                betterBirdCage.killAll();
                freedom.addElement(m);
            } else {
                Monster* m = new Monster(*animal);
                betterFishAq.killAll();
                freedom.addElement(m);
            }
        }

        /*Function that prints specific animal name and daysLived, as TALK command is inputted.*/
        void talk(std::vector<std::string> command) {
            Container<Animal*>* container;
            int pos;
            if (command[1] == "Freedom") {
                container = &this->freedom;
                pos = std::stoi(command[2]);
            } else {
                container = findContainer(command[1], command[2]);
                pos = std::stoi(command[3]);
            }
            if (container) {
                if (container->size() <= pos) {
                    notFound();
                    return;
                } else {
                    Animal* animal = container->getElement(pos);
                    animal->sayName();
                }
            }
        }

        /*Fucntion that removes substance from specific animal's body. It checks if the animal is not in Freedom container, if the required animal is of "Better" class,
        and if the required animal is in the container.*/
        void removeSubstance(std::vector<std::string> command) {
            if (command[1] == "Freedom") {
                std::cout << "Substance cannot be removed in freedom" << std::endl;
                return;
            }
            if (command[2] == "M" || command[2] == "B" || command[2] == "F") {
                std::cout << "Invalid substance removal" << std::endl;
                return;
            }
            Container<Animal*>* container = findContainer(command[1], command[2]);
            if (container) {
                int pos = std::stoi(command[3]);
                if (container->size() <= pos) {
                    notFound();
                    return;
                } else {
                    Animal* animal = container->getElement(pos);
                    container->removeElement(pos);
                    degradeAnimal(animal, command[2], command[1]);
                }
            }
        }

        /*Helper-function that turns objects of "Better" class to object of regular class and places it to the right container.*/
        void degradeAnimal(Animal* animal, std::string type, std::string container) {
            std::string name = animal->getName();
            int doubledDays = animal->getDaysLived() * 2; 
            if (type == "BM") {
                Mouse* ms = new Mouse(name, doubledDays);
                if (container == "Cage") {
                    mouseCage.addElement(ms);
                } else {
                    mouseAq.addElement(ms);
                }
            } else if (type == "BB") {
                Bird* b = new Bird(name, doubledDays);
                birdCage.addElement(b);
            } else {
                Fish* f = new Fish(name, doubledDays);
                fishAq.addElement(f);
            }
        }
        
        /*Function for attacking between animals. It checks, if animals are not in Freedom container and if required animals are in the container.
        When the animal is attacked, it dies and gets removed frmo the container.*/
        void attack(std::vector<std::string> command) {
            if (command[1] == "Freedom") {
                std::cout << "Animals cannot attack in Freedom" << std::endl;
                return;
            }
            Container<Animal*>* container = findContainer(command[1], command[2]);
            if (container) {
                int pos1 = std::stoi(command[3]);
                int pos2 = std::stoi(command[4]);
                if (container->size() <= pos1 || container->size() <= pos2) {
                    notFound();
                    return;
                } else {
                    Animal* attacker = container->getElement(pos1);
                    Animal* prey = container->getElement(pos2);
                    attacker->attack(*prey);
                    container->removeElement(pos2);
                }
            }
        }

        /*Function that incremants daysLived for all animals in all containers and removes dead animals from all containers.*/
        void period() {
            birdCage.incrementDaysForAll();
            birdCage.removeDead();
            
            betterBirdCage.incrementDaysForAll();
            betterBirdCage.removeDead();

            mouseCage.incrementDaysForAll();
            mouseCage.removeDead();

            betterMouseCage.incrementDaysForAll();
            betterMouseCage.removeDead();

            fishAq.incrementDaysForAll();
            fishAq.removeDead();

            betterFishAq.incrementDaysForAll();
            betterFishAq.removeDead();

            mouseAq.incrementDaysForAll();
            mouseAq.removeDead();

            betterMouseAq.incrementDaysForAll();
            betterMouseAq.removeDead();

            freedom.incrementDaysForAll();
            freedom.removeDead();
        }
};        

/*Main function that parses input and pass arguments to the processCommand function in CommandManager.*/
int main() {
    int c;
    std::cin >> c;
    std::cin.ignore();
    std::string command;
    CommandManager manager;
    for (int i = 0; i < c; i++) {
        std::getline(std::cin, command);
        std::vector<std::string> parsedCommand;
        std::stringstream ss(command);
        std::string temp;
        while (getline(ss, temp, ' ')) {
            parsedCommand.push_back(temp);
        }
        manager.processCommand(parsedCommand);
    }
    return 0;
}    