# FastMapper
FastMapper is a kotlin DSL based auto mapping library for kotlin and android applications.

![alt text](https://github.com/Mukuljangir372/fastmapper/blob/master/fastmapper-logo.png)

## FastMapper in 2 Steps
1. Define Mapper
```kotlin
val mapper = FastMapper.Builder.build {
    withMapping<ModelSource, ModelTarget>()
}
```
2. Map Objects
```kotlin
val model = ModelSource(name = "Fastmapper")
val mappedModel: ModelTarget = mapper.map(model)
```
FastMapper also allowed to map fields with custom coordinates.
```kotlin
val mapper = FastMapper.Builder.build {
    withMapping<ModelSource, ModelTarget> {
       ModelSource::name mapTo ModelTarget::bigname
    }
}
```
## Add FastMapper to your project
### Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```kotlin 
allprojects {
   repositories {
       //...
       maven { url 'https://jitpack.io' }
   }
}
```
### Step 2. Add the dependency
```kotlin
dependencies {
    implementation 'com.github.mukuljangir372:fastmapper:v1.0'
}
```
