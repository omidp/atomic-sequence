### Atomic Sequence generator 

Postgres Atomic / Gapless numbered sequence generator

### How to test 

- Install Apache Benchmark (ab)

```
ab -m POST -n 100 -c 10 http://localhost:8080/invoice
```