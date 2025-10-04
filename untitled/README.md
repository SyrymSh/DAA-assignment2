# Kadane's Algorithm Implementation

A comprehensive Java implementation of Kadane's Algorithm for finding the maximum subarray sum, with performance tracking, benchmarking, and empirical analysis capabilities.

## 📋 Table of Contents

- [Algorithm Overview](#algorithm-overview)
- [Complexity Analysis](#complexity-analysis)
- [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Benchmarking](#benchmarking)
- [Testing](#testing)
- [Performance Analysis](#performance-analysis)
- [Project Structure](#project-structure)

## 🧠 Algorithm Overview

Kadane's Algorithm is an efficient solution to the **maximum subarray problem**, which finds the contiguous subarray within a one-dimensional array of numbers that has the largest sum.

### Key Features

- **O(n) Time Complexity**: Single pass through the array
- **O(1) Space Complexity**: Constant extra space
- **Position Tracking**: Returns start and end indices of maximum subarray
- **Performance Metrics**: Tracks comparisons, array accesses, memory usage
- **Multiple Distributions**: Handles various input patterns optimally

### Mathematical Formulation

Given an array `A` of size `n`, find indices `i` and `j` such that:
max_sum = max_{0 ≤ i ≤ j < n} ∑_{k=i}^{j} A[k]