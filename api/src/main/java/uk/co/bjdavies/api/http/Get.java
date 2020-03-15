package uk.co.bjdavies.api.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a get request your controller,
 * Your return type must be either String, IResponse, or <? extends Publisher<Void>>
 * <p>
 * e.g.
 *
 *
 * <code>
 *
 * @author ben.davies99@outlook.com (Ben Davies)
 * @Get() public Flux<Void> all() {
 * return testService.all();
 * }
 * @Get("/{id}") public Mono<Void> findOne(@Param String id) {
 * return testService.findOne(id);
 * }
 * </code>
 * @since 1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Get {
    String value() default "";
}
