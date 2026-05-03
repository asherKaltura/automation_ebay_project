package annotations;

import enums.DataSource;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestData {

    String value();
    // Path to file / query / sheet name (depending on the data source)

    DataSource source();
    // Data source type: CSV, EXCEL, JSON, DB

    String params() default "";
    // Optional parameters in format: key=value,key=value

    int limit() default -1;
    // Maximum number of rows to load (-1 = no limit)

    int sheetIndex() default 0;
    // Excel only: sheet index (default = 0)
}