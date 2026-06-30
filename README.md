<div align="center">
  <a href="https://github.com/islemAZ360/java-thread-pools-performance-comparison">
    <img src="https://readme-typing-svg.demolab.com?font=JetBrains+Mono&weight=800&size=40&duration=3000&pause=1000&color=0EA5E9&center=true&vCenter=true&width=1000&height=70&lines=Java+Concurrency+Benchmark" alt="Main Title" />
  </a>
  <br>
  <img src="https://readme-typing-svg.demolab.com?font=JetBrains+Mono&weight=500&size=20&duration=4000&pause=1000&color=94A3B8&center=true&vCenter=true&width=1000&height=40&lines=FixedThreadPool+vs+CachedThreadPool+vs+Virtual+Threads;Project+Loom+%7C+Java+21+%7C+Load+Simulation" alt="Subtitle" />
  <br><br>

  <a href="https://www.java.com/en/"><img src="https://img.shields.io/badge/Java%2021+-007396?style=flat-square&logo=openjdk&logoColor=white" alt="Java 21" /></a>
  <a href="https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html"><img src="https://img.shields.io/badge/Architecture-Project%20Loom-4CAF50?style=flat-square&logo=java&logoColor=white" alt="Loom" /></a>
  <a href="#"><img src="https://img.shields.io/badge/Concurrency-ExecutorService-0052CC?style=flat-square&logo=apache&logoColor=white" alt="Concurrency" /></a>
  <a href="#"><img src="https://img.shields.io/badge/Testing-Load%20Simulation-D32F2F?style=flat-square&logo=speedtest&logoColor=white" alt="Simulation" /></a>

  <br><br>
  <img src="https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png" width="800">
</div>

## Абстракт

Данное исследование представляет собой глубокий анализ производительности различных архитектурных подходов к многопоточности в среде Java. Основной фокус направлен на сравнение классических пулов потоков (платформенных) с инновационной моделью виртуальных потоков, внедренной в Java 21 (Project Loom).

Архитектура современных высоконагруженных серверных приложений требует максимальной эффективности при работе с блокирующими операциями ввода-вывода (I/O). В рамках данного стенда мы симулируем экстремальную одновременную нагрузку, чтобы выявить сильные и слабые стороны каждого пула.

<div align="center">
  <img src="https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png" width="800">
</div>

## Визуализация производительности

Ниже представлена SVG-диаграмма, генерируемая динамически, которая наглядно демонстрирует разрыв в производительности между традиционными пулами и легковесными виртуальными потоками при обработке 10 000 блокирующих задач.

<div align="center">
  <img src="https://quickchart.io/chart?bkg=transparent&format=svg&width=800&height=300&c=%7B%20type%3A%20%27horizontalBar%27%2C%20data%3A%20%7B%20labels%3A%20%5B%27Virtual%20Threads%20%28833%20ms%29%27%2C%20%27CachedPool%20%28992%20ms%29%27%2C%20%27FixedPool%20%2830%2C896%20ms%29%27%5D%2C%20datasets%3A%20%5B%7B%20data%3A%20%5B833%2C%20992%2C%2030896%5D%2C%20backgroundColor%3A%20%5B%27%2310B981%27%2C%20%27%23F59E0B%27%2C%20%27%23EF4444%27%5D%20%7D%5D%20%7D%2C%20options%3A%20%7B%20legend%3A%20%7B%20display%3A%20false%20%7D%2C%20title%3A%20%7B%20display%3A%20true%2C%20text%3A%20%27Execution%20Time%20%28ms%29%20-%20Lower%20is%20Better%27%2C%20fontColor%3A%20%27%2394A3B8%27%2C%20fontSize%3A%2018%20%7D%2C%20scales%3A%20%7B%20xAxes%3A%20%5B%7B%20ticks%3A%20%7B%20fontColor%3A%20%27%2394A3B8%27%20%7D%2C%20gridLines%3A%20%7B%20color%3A%20%27%23334155%27%20%7D%20%7D%5D%2C%20yAxes%3A%20%5B%7B%20ticks%3A%20%7B%20fontColor%3A%20%27%23CBD5E1%27%2C%20fontSize%3A%2015%20%7D%2C%20gridLines%3A%20%7B%20display%3A%20false%20%7D%20%7D%5D%20%7D%20%7D%20%7D" alt="Benchmark Results Chart" />
</div>

<div align="center">
  <img src="https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png" width="800">
</div>

## Инженерный анализ результатов

### 1. Триумф Virtual Threads (833 ms)
Виртуальные потоки продемонстрировали наивысшую производительность. Их главным преимуществом является то, что они управляются непосредственно виртуальной машиной (JVM), а не операционной системой. При вызове блокирующих операций (таких как `Thread.sleep()` или сетевой запрос), виртуальный поток освобождает платформенный поток, позволяя ему выполнять другие задачи. Затраты памяти и времени на переключение контекста практически нулевые.

### 2. Иллюзия производительности CachedThreadPool (992 ms)
На первый взгляд, кэшированный пул потоков показал отличный результат, близкий к лидеру. Однако этот подход критически опасен в production-среде. Для 10 000 задач пул попытался агрессивно создать почти 10 000 тяжеловесных потоков операционной системы. Под реальной боевой нагрузкой такой подход неизбежно приводит к отказу системы с фатальной ошибкой `OutOfMemoryError: unable to create new native thread`.

### 3. Ограничения FixedThreadPool (30 896 ms)
Фиксированный пул показал наихудшее время. Из-за жесткого лимита в 100 платформенных потоков, остальные 9 900 задач были вынуждены ожидать в очереди. Поскольку каждая задача физически блокировала поток на сотни миллисекунд, пропускная способность пула катастрофически снизилась. Данный подход гарантирует стабильность системы (исключает OOM), но совершенно не масштабируется для огромного количества I/O-интенсивных задач.

<div align="center">
  <img src="https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png" width="800">
</div>

## Как запустить

Проект требует установки **JDK 21** (или выше) для поддержки виртуальных потоков.

```bash
# Клонирование репозитория
git clone https://github.com/islemAZ360/java-thread-pools-performance-comparison.git
cd java-thread-pools-performance-comparison

# Компиляция
javac -d out src/ThreadPoolBenchmark.java

# Запуск
java -cp out ThreadPoolBenchmark
```