Moshi Polymorphic Adapter
=========================

Moshi Polymorphic Adapter is a library written by Kotlin, which provides polymorphic Adapters
for [Moshi](https://github.com/square/moshi)
. It's based on the`PolymorphicJsonAdapterFactory` of Moshi, but more flexible.

### ValuePolymorphicAdapterFactory

A polymorphic adapter factory creates an adapter that uses the unique value to determine which type to decode to.
The `ValuePolymorphicAdapterFactory` is almost same as the `PolymorphicJsonAdapterFactory`, but it supports `Int`
, `Long`, `Double` and `Boolean`, not only `String`.

Suppose we want to decode a JSON object to a Kotlin or a Java type, and the JSON object is like:

```json
[
  {
    "type": 1,
    "typeOneData": "test"
  },
  {
    "type": 2,
    "typeTwoData": 1
  }
]
```

and the type is like:

```kotlin
sealed class Parent(val type: Int)
data class FirstChild(val typeOneData: String) : Parent(1)
data class SecondChild(val typeTwoData: Int) : Parent(2)
```

or

```java
interface Parent {
}

class FirstChild implements Parent {
    String typeOneData;

    public FirstChild(String typeOneData) {
        this.typeOneData = typeOneData;
    }
}

class SecondChild implements Parent {
    int typeTwoData;

    public SecondChild(int typeTwoData) {
        this.typeTwoData = typeTwoData;
    }
}
```

Base types may be classes or interfaces. Subtypes are encoded as JSON objects. Base types don't have to include a type
label like `type`, which is optional.

Configure the adapter factory:

<details open>
<summary>Kotlin</summary>

```kotlin
val valuePolymorphicAdapterFactory = ValuePolymorphicAdapterFactory.of(Parent::class.java, "type", Int::class.java)
    .withSubtype(FirstChild::class.java, 1)
    .withSubtype(SecondChild::class.java, 2)
val moshi = Moshi.Builder().add(valuePolymorphicAdapterFactory).build()
```

</details>

<details>
<summary>Java</summary>

```java
ValuePolymorphicAdapterFactory<Parent, Integer> valuePolymorphicAdapterFactory=ValuePolymorphicAdapterFactory.of(Parent.class,"type",int.class)
        .withSubtype(FirstChild.class,1)
        .withSubtype(SecondChild.class,2);
        Moshi moshi=new Moshi.Builder().add(valuePolymorphicAdapterFactory).build();
```

</details>

### NamePolymorphicAdapterFactory

This adapter factory is also similar to `PolymorphicJsonAdapterFactory`, but it creates an adapter that determines which
type to decode to by the JSON field name, not value.

For example, We have a JSON object that doesn't have a type label field:

```json
 [
  {
    "unique name": 1,
    "commonData": "data",
    "data": "data"
  },
  {
    "uniqueSecondName": 1,
    "commonData": "data",
    "data": 1
  }
]
```

and the type is like:

```kotlin
sealed class Parent
data class FirstChild(@Json(name = "unique name") val uniqueName: Int, val commonData: Sting, val data: String) :
    Parent()
data class SecondChild(val uniqueSecondName: Int, val commonData: Sting, val data: Int) : Parent()
```

or

```java
interface Parent {
}

class FirstChild implements Parent {
    @Json(name = "unique name")
    int uniqueName;
    String commonData;
    String data;

    public FirstChild(int uniqueName, String commonData, String data) {
        this.uniqueName = uniqueName;
        this.commonData = commonData;
        this.data = data;
    }
}

class SecondChild implements Parent {
    int uniqueSecondName;
    String commonData;
    int data;

    public SecondChild(int uniqueSecondName, String commonData, int data) {
        this.uniqueSecondName = uniqueSecondName;
        this.commonData = commonData;
        this.data = data;
    }
}
```

Configure the adapter factory:

<details open>
<summary>Kotlin</summary>

```kotlin
val namePolymorphicAdapterFactory = NamePolymorphicAdapterFactory.of(Parent::class.java)
    .withSubtype(FirstChild::class.java, "unique name")
    .withSubtype(SecondChild::class.java, "uniqueSecondName")
val moshi = Moshi.Builder().add(namePolymorphicAdapterFactory).build()
```

</details>

<details>
<summary>Java</summary>

```java
NamePolymorphicAdapterFactory<Parent> namePolymorphicAdapterFactory=NamePolymorphicAdapterFactory.of(Parent.class)
        .withSubtype(FirstChild.class,"unique name")
        .withSubtype(SecondChild.class,"uniqueSecondName");
        Moshi moshi=new Moshi.Builder().add(namePolymorphicAdapterFactory).build();
```

</details>

### Set the default value or the fallback Adapter

Moshi Polymorphic adapter can set the default value or the fallback adapter using the `withDefaultValue`
and `withFallbackJsonAdapter` methods, which are derived from the `PolymorphicJsonAdapterFactory`. Please refer to
the [PolymorphicJsonAdapterFactory](https://github.com/square/moshi/blob/master/adapters/src/main/java/com/squareup/moshi/adapters/PolymorphicJsonAdapterFactory.java#L98)
for more details.

### Installation

Depend via Maven:

```xml

<dependency>
    <groupId>dev.onenowy.moshipolymorphicadapter</groupId>
    <artifactId>moshi-polymorphic-adapter</artifactId>
    <version>{version}</version>
</dependency>
```

or Gradle:

```kotlin
implementation("dev.onenowy.moshipolymorphicadapter:moshi-polymorphic-adapter:{version}")
```

Kotlin-Sealed
-------------

Moshi Polymorphic Adapter provides convenience ways to create adapters for Kotlin sealed classes. It uses `@JsonClass`
of Moshi and `generator` tag in the annotation to configure the adapter type. `PolymorphicAdapterType` has constant
values that represent the type of the adapter.

For `ValuePolymorphicAdapter`:

```kotlin
@JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_INT + ":" + "type")
sealed class Parent

@ValueLabel(1.toString())
data class FirstChild(val type: Int, val typeOneData: String) : Parent()

@ValueLabel(2.toString())
class SecondChild(val typeTwoData: Int) : Parent()
```

the`generator` tag value must be set as `PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_{value type}:{type label}`.

and for `NamePolymorphicAdapter`:

```kotlin
@JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.NAME_POLYMORPHIC_ADAPTER)
sealed class Parent

@NameLabel("unique Name")
data class FirstChild(@Json(name = "unique name") val uniqueName: Int, val commonData: Sting, val data: String) :
    Parent()

@NameLabel("uniqueSecondName")
data class SecondChild(val uniqueSecondName: Int, val commonData: Sting, val data: Int) : Parent()
```
It supports `null` as a default value using `@DefaultNull`.

```kotlin
@JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.NAME_ADAPTER)
@DefaultNull
sealed class Parent()
```

You can use this feature with reflection or codegen.

### Reflection

To use the reflection feature, you need to add the `KotlinSealedPolymorphicAdapterFactory`. If you
use `KotlinJsonAdapterFactory` of Moshi, the `KotlinSealedPolymorphicAdapterFactory` must be added before it.

```kotlin
val moshi = Moshi.Builder()
    .add(KotlinSealedPolymorphicAdapterFactory())
    .add(KotlinJsonAdapterFactory())
    .build()
```

The reflection feature requires the following additional dependency:

```xml

<dependency>
    <groupId>dev.onenowy.moshipolymorphicadapter</groupId>
    <artifactId>kotlin-sealed-reflect</artifactId>
    <version>{version}</version>
</dependency>
```

```kotlin
implementation("dev.onenowy.moshipolymorphicadapter:kotlin-sealed-reflect:{version}")
```

### Codegen

The codgen feature requires kapt plugin and the following additional dependency:

```xml

<dependency>
    <groupId>dev.onenowy.moshipolymorphicadapter</groupId>
    <artifactId>kotlin-sealed-codegen</artifactId>
    <version>{version}</version>
</dependency>
```

```kotlin
kapt("dev.onenowy.moshipolymorphicadapter:kotlin-sealed-codegen:{version}")
```