package com.example.growpath.data

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that provides dummy documentation content for the app
 */
@Singleton
class DocumentationRepository @Inject constructor() {

    /**
     * Get documentation content for a specific topic
     */
    fun getDocumentationForTopic(topicId: String): DocumentationContent {
        return documentationContent[topicId] ?: fallbackDocumentation
    }

    /**
     * Get all available topics
     */
    fun getAllTopics(): List<DocumentationTopic> {
        return documentationTopics
    }

    companion object {
        // Fallback documentation if a specific topic isn't found
        private val fallbackDocumentation = DocumentationContent(
            title = "General Documentation",
            content = """
                ## Introduction to Programming
                
                Programming is the process of creating a set of instructions that tell a computer how to perform a task. These instructions, known as code, are written in programming languages that the computer can understand and execute. Programming involves designing, writing, testing, debugging, and maintaining the source code of computer applications.
                
                Modern programming involves working with various tools, frameworks, and libraries that make development more efficient. Version control systems like Git help developers track changes, collaborate, and maintain different versions of their codebase. Integrated Development Environments (IDEs) provide features like code completion, debugging tools, and syntax highlighting to enhance productivity.
                
                ## Learning Resources
                
                There are numerous resources available for learning programming, including online courses, tutorials, documentation, and communities. Platforms like Coursera, Udemy, and Codecademy offer structured courses, while websites like Stack Overflow provide a space for asking questions and getting help from experienced developers.
                
                Practice is essential for mastering programming. Start with small projects that interest you and gradually increase the complexity as you gain confidence. Participate in coding challenges and hackathons to apply your skills in different contexts and learn from others.
            """.trimIndent(),
            author = "GrowPath Team",
            lastUpdated = "2025-05-15"
        )

        // List of documentation topics
        private val documentationTopics = listOf(
            DocumentationTopic("android_basics", "Android Basics"),
            DocumentationTopic("kotlin_fundamentals", "Kotlin Fundamentals"),
            DocumentationTopic("jetpack_compose", "Jetpack Compose"),
            DocumentationTopic("architecture_patterns", "Architecture Patterns"),
            DocumentationTopic("testing", "Testing in Android")
        )

        // Map of topic IDs to documentation content
        private val documentationContent = mapOf(
            "android_basics" to DocumentationContent(
                title = "Android Basics",
                content = """
                    ## Introduction to Android Development
                    
                    Android development is the process of creating applications for devices running the Android operating system. Android apps are written primarily in Kotlin or Java and utilize the Android SDK (Software Development Kit) to access device functionality. The Android platform provides a rich set of libraries, tools, and frameworks that make it easier to build feature-rich applications.
                    
                    As of 2025, Android powers billions of devices worldwide, including smartphones, tablets, watches, TVs, and cars. This vast ecosystem offers developers a massive potential audience for their apps. Understanding Android development fundamentals is essential for building applications that work across different device types and screen sizes.
                    
                    ## Key Components of Android Apps
                    
                    Android applications are composed of several key components that work together to create a cohesive user experience. Activities represent single screens with a user interface, while Services perform operations in the background. Content Providers manage access to structured data, and Broadcast Receivers respond to system-wide broadcast announcements.
                    
                    The AndroidManifest.xml file is a crucial part of every Android application. It declares the app's components, permissions, hardware and software requirements, and other configuration details. Understanding how to properly configure this file is essential for ensuring your app functions correctly across different devices and Android versions.
                    
                    ## Modern Android Development
                    
                    Modern Android development has evolved significantly in recent years with the introduction of Jetpack libraries, Kotlin, and Jetpack Compose. These tools help developers write more concise, maintainable code and create beautiful, responsive user interfaces with less effort.
                    
                    Following best practices such as implementing proper architecture patterns (like MVVM or Clean Architecture), writing unit tests, and handling device configuration changes properly will help you build robust, maintainable Android applications. Staying up-to-date with the latest Android development trends and tools is essential for remaining competitive in the fast-moving mobile development landscape.
                """.trimIndent(),
                author = "Android Team at GrowPath",
                lastUpdated = "2025-05-20"
            ),

            "kotlin_fundamentals" to DocumentationContent(
                title = "Kotlin Fundamentals",
                content = """
                    ## Introduction to Kotlin
                    
                    Kotlin is a modern, statically typed programming language that runs on the Java Virtual Machine (JVM). It was developed by JetBrains and is now the preferred language for Android development. Kotlin combines object-oriented and functional programming features, offering significant advantages over Java such as null safety, extension functions, and more concise syntax.
                    
                    One of Kotlin's primary strengths is its interoperability with Java, allowing developers to use existing Java libraries and frameworks seamlessly. This interoperability makes transitioning from Java to Kotlin relatively straightforward and allows for gradual adoption in existing projects. Kotlin also addresses many pain points of Java, such as verbose syntax and the lack of certain modern language features.
                    
                    ## Key Language Features
                    
                    Null safety is one of Kotlin's most celebrated features. By distinguishing between nullable and non-nullable types at the compiler level, Kotlin helps prevent null pointer exceptions, one of the most common sources of runtime errors in Java. The language provides safe operators like the Elvis operator (?:) and the safe call operator (?.) to handle nullable values elegantly.
                    
                    Kotlin's extension functions allow developers to add new functionality to existing classes without inheriting from them. This powerful feature enables cleaner, more readable code by allowing functions to be called with the familiar dot notation while keeping related functionality together. Coroutines provide a way to write asynchronous, non-blocking code in a sequential style, greatly simplifying complex asynchronous operations.
                    
                    ## Best Practices in Kotlin
                    
                    Following Kotlin idioms and best practices helps you write more concise, readable, and maintainable code. These include using data classes for model objects, taking advantage of the when expression for more readable conditional logic, and using scoping functions like let, with, and apply to make your code more expressive.
                    
                    As you advance in Kotlin, explore higher-order functions, inline functions, and DSL (Domain-Specific Language) creation to make your code even more powerful and expressive. Understanding these advanced concepts can help you fully leverage Kotlin's capabilities and write more elegant solutions to complex problems.
                """.trimIndent(),
                author = "Kotlin Expert at GrowPath",
                lastUpdated = "2025-05-18"
            ),

            "jetpack_compose" to DocumentationContent(
                title = "Jetpack Compose",
                content = """
                    ## Introduction to Jetpack Compose
                    
                    Jetpack Compose is Android's modern toolkit for building native user interfaces. It simplifies and accelerates UI development by using a declarative programming model, where you describe what your UI should look like based on the current state, and the framework handles updating the UI when the state changes. This approach results in less code, fewer bugs, and more intuitive UI construction compared to traditional XML-based layouts.
                    
                    Released as stable in 2021, Compose has quickly become the recommended approach for building Android UIs. It's designed from the ground up for the reactive programming model and works seamlessly with other Jetpack libraries. By 2025, Compose has matured significantly with robust support for animations, accessibility, testing, and complex UI patterns.
                    
                    ## Core Concepts
                    
                    Composable functions are the building blocks of Compose UI. These functions take some input and emit UI elements, following a reactive programming model where the UI automatically updates when the input data changes. Compose uses a technique called "recomposition" to efficiently update only the parts of the UI that need to change when data updates.
                    
                    State management is a critical concept in Compose. The framework provides several ways to handle state, including remember, mutableStateOf, and derivedStateOf. Understanding how state flows through your Compose hierarchy and how to manage it effectively is essential for building responsive, efficient UIs that correctly reflect your application's data.
                    
                    ## Advanced Compose Techniques
                    
                    As you become more proficient with Compose, you'll want to explore more advanced techniques like custom layouts, custom drawing with Canvas, and creating complex animations. Compose's flexible nature allows for highly customized UI components that would be difficult or impossible to create with traditional View-based UI.
                    
                    Performance optimization becomes important as your Compose UIs grow more complex. Techniques like key assignment, smart recomposition control with remember and derivedStateOf, and avoiding unnecessary object allocations in the composition phase can help keep your UI smooth and responsive, even for complex screens with many UI elements and frequent updates.
                """.trimIndent(),
                author = "Compose Expert at GrowPath",
                lastUpdated = "2025-05-25"
            ),

            "architecture_patterns" to DocumentationContent(
                title = "Architecture Patterns",
                content = """
                    ## Introduction to Architecture Patterns
                    
                    Architecture patterns are reusable solutions to common problems in software design. They help developers create organized, maintainable, and testable applications by defining how different components interact with each other. Good architecture separates concerns, making the codebase easier to understand, test, and modify over time.
                    
                    In Android development, following established architecture patterns is particularly important due to the complexity of mobile applications and the unique lifecycle of Android components. A well-architected Android app can better handle configuration changes, process death, and the complexities of asynchronous operations while providing a smooth user experience.
                    
                    ## Popular Architecture Patterns
                    
                    Model-View-ViewModel (MVVM) has become the standard architecture pattern for Android applications, especially when combined with Android Architecture Components like ViewModel, LiveData, and Room. MVVM separates the UI (View) from the business logic and data handling (ViewModel), with the Model representing the data and business rules.
                    
                    Clean Architecture takes separation of concerns further by dividing the application into distinct layers: presentation, domain, and data. Each layer has a specific responsibility and depends only on inner layers, creating a more maintainable and testable codebase. While more complex to implement initially, Clean Architecture offers significant benefits for larger applications that need to scale over time.
                    
                    ## Implementing Architecture in Android
                    
                    When implementing architecture patterns in Android, leverage the Architecture Components provided by Android Jetpack. ViewModels help manage UI-related data through configuration changes, while LiveData or Flow provide lifecycle-aware observable data. Room simplifies database operations, and Navigation Component helps with fragment transactions and deep linking.
                    
                    Dependency injection is a crucial companion to good architecture. Libraries like Hilt or Koin make it easier to provide dependencies to different components, enabling better testability and modularity. Properly implemented dependency injection allows for more flexible code that's easier to test with mock implementations.
                """.trimIndent(),
                author = "Architecture Team at GrowPath",
                lastUpdated = "2025-05-10"
            ),

            "testing" to DocumentationContent(
                title = "Testing in Android",
                content = """
                    ## Introduction to Android Testing
                    
                    Testing is a critical part of the software development process that helps ensure your application works as expected and remains stable through updates and changes. In Android development, a comprehensive testing strategy includes unit tests, integration tests, and UI tests, each serving a different purpose in verifying application correctness.
                    
                    Investing time in testing pays dividends by catching bugs early, enabling confident refactoring, and providing documentation of expected behavior. Modern Android testing tools make it easier than ever to write and run tests, allowing developers to deliver higher quality applications with fewer regressions and critical bugs.
                    
                    ## Types of Tests
                    
                    Unit tests verify individual components in isolation, typically focusing on a single class or function. They run on the JVM without the need for an Android device or emulator, making them fast and efficient. Libraries like JUnit, Mockito, and MockK are commonly used for writing effective unit tests that verify business logic, utility functions, and other non-UI components.
                    
                    Instrumented tests run on an Android device or emulator and test components that depend on the Android framework. UI tests are a type of instrumented test that verifies user interactions and UI behaviors. Espresso is the primary library for UI testing, allowing developers to simulate user actions and verify that the correct UI elements are displayed with the expected content.
                    
                    ## Testing Best Practices
                    
                    Follow the testing pyramid approach, with many unit tests, fewer integration tests, and a smaller number of UI tests. This strategy provides good coverage while keeping the test suite fast and maintainable. Write tests that focus on behavior rather than implementation details to allow for code refactoring without breaking tests.
                    
                    Make your code testable by following principles like dependency injection, separation of concerns, and avoiding static methods or singleton abuse. Testable code is typically better designed and more maintainable. Consider using Test-Driven Development (TDD) for critical or complex components, writing tests before implementation to ensure the code meets requirements.
                """.trimIndent(),
                author = "QA Team at GrowPath",
                lastUpdated = "2025-05-05"
            )
        )
    }
}

/**
 * Data class representing a documentation topic
 */
data class DocumentationTopic(
    val id: String,
    val title: String
)

/**
 * Data class representing documentation content
 */
data class DocumentationContent(
    val title: String,
    val content: String,
    val author: String,
    val lastUpdated: String
)
