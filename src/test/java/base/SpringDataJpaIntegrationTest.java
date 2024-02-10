package base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * We favor using {@code @SpringBootTest} over {@code @DataJpaTest} to test repositories and services. We understand
 * that starting the full Spring context instead is closer to production, improves the reliability of tests, minimizes
 * cognitive load, and is faster when running the whole test suite. <br/><br/>
 *
 * But if you still prefer to work with the {@code @DataJpaTest} slice, we recommend using this base class also
 * configured by us:
 * https://gist.github.com/rponte/385838088f64ab8004ba7d15de80ca34#file-_springdatajpaintegrationtest-java-L26
 */
public abstract class SpringDataJpaIntegrationTest extends SpringBootIntegrationTest {

    /**
     * (!!!) It does NOT work properly when the transactional context is disabled.
     *       You should use the repositories instead or combine it with {@code TransactionTemplate} for example.
     */
    @Autowired
    private EntityManager entityManager;

    @Autowired
    protected TransactionTemplate transactionTemplate;

    /**
     * Executes the {@code function} inside a transactional context and returns its result
     */
    public <T> T doInTransaction(JpaTransactionFunction<T> function) {
        function.beforeTransactionCompletion();
        try {
            return transactionTemplate.execute(status -> {
                T result = function.apply(entityManager);
                return result;
            });
        } finally {
            function.afterTransactionCompletion();
        }
    }

    /**
     * Executes the {@code function} inside a transactional context but does not return anything
     */
    public void doInTransaction(JpaTransactionVoidFunction function) {
        function.beforeTransactionCompletion();
        try {
            transactionTemplate.executeWithoutResult(status -> {
                function.accept(entityManager);
            });
        } finally {
            function.afterTransactionCompletion();
        }
    }

    @FunctionalInterface
    protected interface JpaTransactionFunction<T> extends Function<EntityManager, T> {
        default void beforeTransactionCompletion() {

        }

        default void afterTransactionCompletion() {

        }
    }

    @FunctionalInterface
    protected interface JpaTransactionVoidFunction extends Consumer<EntityManager> {
        default void beforeTransactionCompletion() {

        }

        default void afterTransactionCompletion() {

        }
    }

}
