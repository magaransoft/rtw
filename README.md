# RTW [![Tests & Docs](https://github.com/magaransoft/rtw/actions/workflows/ci.yml/badge.svg)](https://github.com/magaransoft/rtw/actions/workflows/ci.yml)

A pipeline library for Scala 3 that separates reads, writes, and transforms into distinct stages — each running in its own operation context (e.g., read-only DB connection vs. write connection).

Designed for reader/writer separated architectures such as Amazon RDS clusters, where read and write operations must use different database endpoints.

## Installation

RTW is published to Maven Central. Add the following to your `build.sbt`:

```scala
libraryDependencies += "com.magaran" %% "rtw" % "0.1.0"
```

## Why RTW?

In architectures with separated read/write databases (e.g., RDS read replicas), service methods often need to:

1. **Read** data from the read replica
2. **Transform** the data (validation, business logic, no DB needed)
3. **Write** results to the primary database

Each stage needs a different connection context. RTW makes the stage boundaries visible in your code — you can see at a glance which operations happen in a read context, which in a write context, and which need no database at all.

> **Note:** RTW is a legacy-style approach we used at the company before we learned how to enforce operation contexts through the type system. We plan to release a successor library that makes the context explicit in types — causing compile errors when a read or write call is made in the wrong context.

## Quick Example

```scala
import com.magaran.rtw.*
import com.magaran.typedmap.TypedMap

// Define your operation context provider (e.g., wrapping DB connections)
class MyService extends ReadTransformWritePipeline[TypedMap] {

  def withinReadContext[A](block: => A): A = {
    // Open read-only connection, execute block, close
    block
  }

  def withinWriteContext[A](block: => A): A = {
    // Open write connection, execute block, close
    block
  }

  def processOrder(orderId: Long): String = {
    val pipeline =
      onReadStage() {
        // Runs with read-only DB connection
        fetchOrder(orderId)
      } intoTransformStage { order =>
        // Runs without any DB connection
        validateAndEnrich(order)
      } intoWriteStage { enrichedOrder =>
        // Runs with write DB connection
        saveOrder(enrichedOrder)
        s"Order $orderId processed"
      }

    pipeline.execute
  }
}
```

## Key Concepts

| Concept | Description |
|---------|-------------|
| **`ReadTransformWritePipeline`** | Main trait — provides `onReadStage`, `onWriteStage`, `onTransformStage` |
| **`PipelineBlock`** | An infallible unit of work that produces a value |
| **`FalliblePipelineBlock`** | A pipeline block that may short-circuit on preprocessor failure |
| **`OperationContextProvider`** | Defines how read/write contexts are provided (e.g., DB connections) |
| **`ResultBuilder`** | Assembles the final result from pipeline output and metadata |
| **`FalliblePreProcessor`** | Validates or transforms input before a stage, with early exit on failure |

## License

MIT
