// Da usare solo con versione 2.0 o successive di Idea
// Usa il database H2

// modificare qui il nome del database
def dataBase = 'activation'

dataSource {
    pooled = true
    username = "root"
    password = ""
} // end of dataSource

hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
} // end of hibernate

// environment specific settings
environments {
    development {
        dataSource {
            driverClassName = "com.mysql.jdbc.Driver"
            dbCreate = "update" // one of 'create', 'create-drop','update'
            url = "jdbc:mysql://localhost/${dataBase}?useUnicode=yes&characterEncoding=UTF-8"
            //driverClassName = "org.h2.Driver"
            //dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
            //url = "jdbc:h2:mem:devDb;MVCC=TRUE"
        }
    }
    test {
        dataSource {
            driverClassName = "org.h2.Driver"
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE"
        }
    }
    production {
        dataSource {
            driverClassName = "com.mysql.jdbc.Driver"
            dbCreate = "update" // one of 'create', 'create-drop','update'
            url = "jdbc:mysql://localhost/${dataBase}?useUnicode=yes&characterEncoding=UTF-8"
        } // end of dataSource
    } // end of production

} // end of environments
