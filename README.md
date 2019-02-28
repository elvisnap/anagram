# Anagram

This app finds all anagrams from a dictionary for provided word.
Search is case-insensitive. 

## Installation

Use Maven to create jar file.

```
mvn package
```

## Usage

```
java -jar .\target\anagram-search-0.0.1.jar {fullPathToDictionaryFile} {searchWord}
```

Example

```
java -jar .\target\anagram-search-0.0.1.jar C:\dictionary.txt maja 
```

Output (duration,list,of,anagrams,found):

```
43978,jaam,ajam,jama
```fullPathToDictionaryFile