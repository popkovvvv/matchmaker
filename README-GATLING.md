## Running Gatling

To run tests, run the following command

```bash
   ./gradlew gatlingRun 
```


## Short Report

```javascript
================================================================================
---- Global Information --------------------------------------------------------
> request count                                      11475 (OK=11475  KO=0     )
> min response time                                      6 (OK=6      KO=-     )
> max response time                                  16308 (OK=16308  KO=-     )
> mean response time                                  4659 (OK=4659   KO=-     )
> std deviation                                       3963 (OK=3963   KO=-     )
> response time 50th percentile                       4348 (OK=4349   KO=-     )
> response time 75th percentile                       7023 (OK=7023   KO=-     )
> response time 95th percentile                      12447 (OK=12438  KO=-     )
> response time 99th percentile                      14634 (OK=14634  KO=-     )
> mean requests/sec                                 191.25 (OK=191.25 KO=-     )
---- Response Time Distribution ------------------------------------------------
> t < 800 ms                                          2600 ( 23%)
> 800 ms < t < 1200 ms                                 955 (  8%)
> t > 1200 ms                                         7920 ( 69%)
> failed                                                 0 (  0%)
================================================================================
```

# Report file

```
Please open the following file: {your_file_path}
```
