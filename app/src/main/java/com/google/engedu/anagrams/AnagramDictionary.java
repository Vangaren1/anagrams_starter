/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private ArrayList<String> wordList;
    private HashSet<String> wordSet;
    private HashMap<String,ArrayList> lettersToWord;
    private HashMap<Integer, ArrayList> sizeToWord;
    private int wordListSize;
    private int wordLength = DEFAULT_WORD_LENGTH;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        String word;
        ArrayList tmp;
        int wordLen;

        // milestone 1, create wordList
        wordList = new ArrayList<String>();

        // milestone 2, create wordSet and lettersToWord
        wordSet = new HashSet<String>();
        lettersToWord = new HashMap<String,ArrayList>();

        // milestone 3, create sizeToWord
        sizeToWord = new HashMap<Integer, ArrayList>();

        while((line = in.readLine()) != null) {
            word = line.trim();

            // add word to wordList
            wordList.add(word);

            // add word to wordSet
            wordSet.add(word);

            wordLen = word.length();
            // add word to sizeToWord
            if(sizeToWord.containsKey(wordLen))
            {
                tmp = sizeToWord.get(wordLen);
                tmp.add(word);
                sizeToWord.replace(wordLen, tmp);
                Log.d("adding_to_sizeToWord1", Integer.toString(wordLen)+" "+word);
            }
            else
            {
                tmp = new ArrayList();
                tmp.add(word);
                sizeToWord.put(wordLen,tmp);
                Log.d("adding_to_sizeToWord2", Integer.toString(wordLen)+" "+word);
            }


            String sorted = sortLetters(word);

            if(lettersToWord.containsKey(sorted)){
                tmp = lettersToWord.get(sorted);
                tmp.add(word);
                lettersToWord.replace(sorted, tmp);
            }
            else{
                tmp = new ArrayList();
                tmp.add(word);
                lettersToWord.put(sorted, tmp);
            }


        }
        wordListSize = wordList.size();

        // preprocess the lettersToWord to make sure each entry has enough anagrams in it
//TODO
//        for( String l : lettersToWord)
//        {
//            tmp =
//        }
    }

    public boolean isGoodWord(String word, String base){
        word = word.toLowerCase();
        Log.d("isGoodWord_called", word+" "+base );

        // if the word provided contains the base word, return false
        if( word.contains(base) )
            return false;

        // see if the word does not exist in the wordset, return false
        if(!wordSet.contains(word)) {
            Log.d("isGoodWord_contains", "false");
            return false;
        }

        // If we reach here, then the word exists and doesn't contain the base word
        Log.d("isGoodWord", "true ");
        return true;
    }

    public List<String> getAnagrams(String targetWord) {
        Log.d( "j-term", targetWord);
        ArrayList<String> result = new ArrayList<String>();
        String tmp;
        for( int i=0; i<wordListSize; i++)
        {
            // get the ith item in the word list.
            tmp = wordList.get(i);

            // if its a good word, add it to results
            if(  isGoodWord(tmp, targetWord) )
                result.add(tmp);
        }
        return result;
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        /*
         * add each letters a-z to the word, compute the
         * sorted word, then if it exists in the set, add all the words
         * made that are anagrams for that sorted word.
         */
        String lwrCase = "abcdefghijklmnopqrstuvwxyz";
        String tmpW, tmpSorted;
        char tmpC;
        for(int i = 0; i < 26; i++)
        {

            ArrayList<String> tmpArray;

            tmpC = lwrCase.charAt(i);
            tmpW = word + tmpC;
            tmpSorted = sortLetters(tmpW);

            if(lettersToWord.containsKey(tmpSorted))
            {
                // if the key already exists in lettersToWord,
                // add each of the words in the List of Words
                // to the result set
                tmpArray = lettersToWord.get(tmpSorted);
                Log.d("1moreLetter.found", "sorted word key found: "+tmpSorted);
                Log.d("1moreLetter.found", "tmparraySize: "+Integer.toString(tmpArray.size()));
                for(int j = 0; j<tmpArray.size(); j++) {
                    result.add(tmpArray.get(j));
                    Log.d("1moreLetter.adding", tmpArray.get(j));
                }
            }
            Log.d( "wordPlus1", tmpSorted);
        }

        return result;
    }

    public ArrayList<String> processWordList(ArrayList<String> wList)
    {
        String sorted;
        ArrayList<String> sList;
        int numAna;
        int wListSize = wList.size();

        for( String word : wList)
        {
            // for each word in the list, if it has less than the minimum number of anagrams,
            // drop it from the list
            sorted = sortLetters(word);

            // temporarily store the list of words from lettersToWord in sList to check size
            sList = lettersToWord.get(sorted);
            numAna = sList.size();

            if(numAna < MIN_NUM_ANAGRAMS)
            {
                wList.remove(word);
            }

        }

        return wList;
    }


    public String pickGoodStarterWord() {
        Random rand = new Random();
        // until a good random word is found, loop

        int index, anagramCount;
        String testWord, testWordSorted;

        // tmpList will be the list of Words of Size X
        // tmpArray is the list of anagrams for a particular sorted word
        ArrayList<String> tmpArray, tmpList;

        while(true){
            // get the list of words to pick from
            tmpList = new ArrayList<String>();
            tmpList = sizeToWord.get(wordLength);
            Log.d("listOfWords1", Integer.toString(tmpList.size()) + " " + wordLength);

            // if the list contains no words, increment the wordLength and try again
            if(tmpList.size()!=0) {

                // pick a random word in the word List
                index = rand.nextInt(tmpList.size() );

                testWord = tmpList.get(index);

                Log.d("gettingTestWord", "wordLength: "+Integer.toString(wordLength));
                Log.d("gettingTestWord", "tmpList.size: "+Integer.toString(tmpList.size()));
                Log.d("gettingTestWord", "index: "+Integer.toString(index));
                Log.d("gettingTestWord", "checking testWord: "+testWord);

                // check how many anagrams it has
                testWordSorted = sortLetters(testWord);
                // place the array of anagram words into tmpArray
                tmpArray = lettersToWord.get(testWordSorted);

                // if the word has the minimum number of anagrams, return it
                if (tmpArray.size() >= MIN_NUM_ANAGRAMS) {
                    // if we've found a word that's valid, increment wordLength
                    // so that the next time this is called, we find a bigger word
                    Log.d("incrementWordLength1", "wordLength: "+Integer.toString(wordLength));
                    wordLength++;
                    return testWord;
                }
                else
                {
                    // if the tmpArray doesn't contain the minimum number of anagrams,
                    // print this to the log
                    Log.d("gettingTestWord", "doesn't contain enough anagrams");
                }
            }
            else
            {
                // if there were no words of that length in sizeToWord,
                // on the next loop we'll look for a larger word.
                Log.d("incrementWordLength2", "wordLength: "+Integer.toString(wordLength));
                wordLength++;
            }
        }
    }

    public String sortLetters( String word ){
        char temp[] = word.toCharArray();
        Arrays.sort(temp);
        return new String(temp);
    }
}
