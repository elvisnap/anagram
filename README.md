# Anagram

This app finds all anagrams from a dictionary for provided word.
Search is case-insensitive. 

## Installation

Use Maven to create jar file.

```
mvn clean package
```

## Usage

```
java -jar {location of anagram-search-0.0.1.jar} {fullPathToDictionaryFile} {searchWord}
```

Example

```
java -jar anagram-search-0.0.1.jar C:\dictionary.txt maja 
```

Output (duration,list,of,anagrams,found):

```
43978,jaam,ajam,jama
```