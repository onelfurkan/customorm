package orm.main;

import annotations.EnableTableCreation;
import orm.entity.Person;
import orm.exception.EntityOperationException;
import orm.initializer.DbTableInitializer;
import orm.jparepository.JpaRepository;
import orm.sql.*;

import java.util.List;

@EnableTableCreation(entityPackage = "orm.entity")
public class Application
{
    /**
     * In this example orm framework project we focussed on concept of orm, reflection and custom annotations.
     * You should remove your db table of Person entity before run
     * this test class since Id generator starts from 0 when application restarted.
     * So If u try to run without cleaning your table you will get exception
     * since app will try yo save person entities with same entity id(PK)
     * just because id generator starts from 0 when you restart app.
     * If u want to solve this problem you have to change Id generator structure.
     * In this example project, we focused on orm framework development, not id generation.
     * This is an example project, so you can enhance all part of project.
     */
    public static void main(String[] args) throws Exception
    {

        DDLQueryGenerator ddlQueryGenerator =  new DDLQueryGenerator();
        IQueryGenerator<Person> queryGenerator =  new QueryGenerator<>();
        IStatementGenerator<Person,Long> statementGenerator =  new ISqlStatementGenerator<>();
        DbTableInitializer dbTableInitializer = new DbTableInitializer(ddlQueryGenerator);
        dbTableInitializer.createEntityTables();

        JpaRepository<Person,Long> personRepository = new JpaRepository<>(queryGenerator,statementGenerator);

        Person person1 =  new Person("Fatih","Turk",26);
        Person person2 = new Person("Mehmet", "Yıldız",32);
        Person person3 = new Person("Selim", "Turan",43);
        Person person4 = new Person("Yavuz", "Turkoglu",38);

        // insert
        personRepository.save(person1);
        personRepository.save(person2);
        personRepository.save(person3);
        personRepository.save(person4);

        // findById example
        System.out.println();
        System.out.println("*** FIND BY ID ***");
        Person p3 = personRepository.findById(Person.class,3L);
        System.out.println(p3.toString());

        // print db after insert
        System.out.println();
        System.out.println("**** AFTER INSERT ****");
        printAllPersonInDb(personRepository);


        // update age of person , Person id = 1
        Person p1 = personRepository.findById(Person.class, 1L);
        p1.setAge(99);
        personRepository.update(p1);

        // print db after update
        System.out.println();
        System.out.println("**** AFTER UPDATE age= 99 ****");
        printAllPersonInDb(personRepository);

        // removing person3
        System.out.println();
        System.out.println("*** AFTER REMOVE "+person3.toString()+" ***");
        personRepository.remove(p3);
        printAllPersonInDb(personRepository);


    }

    private static void printAllPersonInDb(JpaRepository personRepository) throws EntityOperationException
    {
        System.out.println();
        // FIND ALL
        List<Person> allPerson = personRepository.findAll(Person.class);

        for(Person p : allPerson)
        {
            System.out.println(p.toString());
        }
    }
}
