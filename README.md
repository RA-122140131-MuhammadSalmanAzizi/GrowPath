# GrowPath

A mobile application to track personal growth journeys through interactive roadmaps, milestones, and achievements.

## Features

- Authentication (Google and Email)
- Interactive Roadmap Graph
- Milestone Tracking with Journal Notes
- User Profile Management
- Experience and Achievement System
- Offline Support
- Notifications

## Tech Stack

- MVVM Architecture
- Jetpack Compose for UI
- Kotlin Programming Language
- Hilt for Dependency Injection
- Room for Local Database Storage
- Firebase (Authentication, Firestore, Storage)
- Clean Architecture (Data, Domain, Presentation layers)

## Project Structure

The project follows a modular approach with feature-based packages and clean architecture principles:
- `data`: Contains repositories, data models, and data sources (local and remote)
- `domain`: Contains use cases and business logic
- `presentation`: Contains UI components, ViewModels, and states
- `di`: Contains dependency injection modules
- `utils`: Contains utility classes and extension functions
- `navigation`: Contains navigation components

## Getting Started

1. Clone the repository
2. Open the project in Android Studio
3. Connect your Firebase project
4. Build and run the application
