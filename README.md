# Slim Down Your Aggregate Devoxx Greece 2024 

Let's slim down some complex aggregate implementation. 

What's Aggregate Pattern?

> _"Cluster the entities and value objects into aggregates and define boundaries around each. Choose one entity to be the root of each aggregate, and allow external objects to hold references to the root only (references to internal members passed out for use within a single operation only). Define properties and invariants for the aggregate as a whole and give enforcement responsibility to the root or some designated framework mechanism."_

> **Eric Evans**, Domain-Driven Design Reference: Definitions and Pattern Summaries

We'll work on an aggregate responsible for managing the book writing, editing and publishing process. See the original implementation in [./src/main/java/io/eventdriven/slimdownaggregates/original/Book.java](./src/main/java/io/eventdriven/slimdownaggregates/original/Book.java).

We'll be doing multiple transitions step by step to make it more focused on the business logic and make it smaller but more precise.

## Prerequisities

1. Clone this repository.
2. Install Java JDK 17 (or later) - https://www.oracle.com/java/technologies/downloads/.
3. Install IntelliJ, Eclipse, VSCode or other preferred IDE.
4. Open main folder as project.
