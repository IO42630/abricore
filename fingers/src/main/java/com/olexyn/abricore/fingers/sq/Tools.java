package com.olexyn.abricore.fingers.sq;


import com.olexyn.abricore.util.Execute;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
    private final Execute x;

    public Tools() {
        x = new Execute();
    }


    /**
     * @param path
     * @return the contents of File at *path* a List of String.
     */
    public List<String> catFileToList(String path) {

        List<String> lines = null;
        try {

            lines = Files.readAllLines(Paths.get(path));
            String br = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }


    public List<String> getItems(List<String> input) {

        List<String> items = new ArrayList<>();

        String readState = "none";
        StringBuilder tmpItem = new StringBuilder();

        for (int i = 0; i < input.size(); i++) {

            String line = input.get(i);

            if (readState.equals("none") && line.contains("<item>")) {
                readState = "reading";
                String[] split = line.split("<item>");
                if (split.length > 0) {
                    tmpItem.append(split[1]);
                }
            }

            if (readState.equals("reading")) {
                if (line.contains("</item>")) {
                    tmpItem.append(line.split("</item>")[0] + "</item>");
                    items.add(tmpItem.toString());
                    tmpItem = new StringBuilder();
                    readState = "none";
                } else {
                    tmpItem.append(line);
                }
            }
        }


        return items;
    }


    /**
     * To be used after "matchRegEx".
     *
     * @param matches
     * @param match
     * @param group
     * @return the n-th group of n-th match of matches.
     */
    public String getGroup(List<List<String>> matches,
                           int match,
                           int group) {
        if (matches.size() > match) {
            if (matches.get(match).size() > group) {
                return matches.get(match).get(group);
            }
        }
        return null;

    }


    /**
     * See @matchRegEx for details.
     * return n-th group of every match.
     * group 0 is the full match.
     */
    public List<String> getGroups(List<List<String>> matches,
                                  int group) {
        List<String> groups = new ArrayList<>();

        for (int i = 0; i < matches.size(); i++) {
            groups.add(matches.get(i).get(group));
        }
        return groups;
    }







    /**
     * premade method, that matches the payload of a tag.
     */
    public String parseTag(String input,
                           String tag) {

        int firstTagStart = input.indexOf("<"+tag);
        int firstTagEnd = input.indexOf(">", firstTagStart);
        int secondTagStart = input.indexOf("</"+tag,firstTagEnd);
        if(firstTagStart >=0 && firstTagEnd >=0 && secondTagStart >=0){
            return input.substring(firstTagEnd+1,secondTagStart);
        } else{
            return null;
        }
    }


    /**
     *
     * @param input
     * @param tag
     * @return The payloads of tags as list.
     */
    public List<String> parseTagS(String input, String tag){
        List<String> ouput = new ArrayList<>();

        int firstTagStart = 0;
        int firstTagEnd = 0;
        int jumpTagStart = 0;
        int jumpTagEnd =0;
        int secondTagStart = 0;
        int secondTagEnd = 0;
        int tagJump = 0; // how many tags were jumped e.g. in <foo> <foo> </foo> </foo>

        while (firstTagStart>=0){

            firstTagStart = input.indexOf("<"+tag, secondTagEnd);
            firstTagEnd = input.indexOf(">", firstTagStart);

            jumpTagStart = input.indexOf("<"+tag, firstTagEnd);
            jumpTagEnd = input.indexOf(">", jumpTagStart);

            secondTagStart = input.indexOf("</"+tag,firstTagEnd);
            secondTagEnd =  input.indexOf(">", secondTagStart);

            if (jumpTagStart > secondTagStart){

            }




            if (firstTagStart >=0 && firstTagEnd >=0 && secondTagStart >=0){
                ouput.add(input.substring(firstTagEnd+1,secondTagStart));
            }
            firstTagStart = input.indexOf("<"+tag, secondTagStart);
        }
        return ouput;
    }


    /**
     * This method returns a List of "Matches".
     * Here a "Match" is a List of String.
     * The entry of a "Match" is the Full match.
     * The second entry of a "Match" is the first Group.
     * The third entruy is the second Group, and so on.
     *
     * @param input input String
     * @param regex pattern String
     * @return matches for pattern, separated by \n
     */
    public List<List<String>> matchRegEx(String input,
                                         String regex) {
        // Text: <ul><li>first</li><li>second</li><li>third</li></ul>
        // Regex: <li>(.*?)<\/li>
        // Match 1 Entry 0: <li>first</li>
        // Match 1 Entry 1: first
        // Match 1 Entry 0: <li>first</li>
        // Match 1 Entry 1: first


        List<List<String>> matches = new ArrayList<>();

        // guess the number of groups
        int groupN = regex.split("\\(").length;


        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(input);
        while (m.find()) {
            List<String> match = new ArrayList<>();

            for (int i = 0; i < groupN; i++) {
                try {
                    match.add(m.group(i));
                } catch ( Exception e){
                    System.out.println(e);
                }
            }
            matches.add(match);
        }

        return matches;
    }


    public String pruneTags(String in) {
        int start = in.indexOf(">") + 1;
        int end = in.indexOf("<", 1);
        return in.substring(start, end);

    }









    /**
     * This method replaces the HTML-notation of certain ASCII chars with their symbol.<p>
     * It was created to transform the XML-notation of the description-field of Jira-issues.<p>
     * It might prove useful for other purposes.
     *
     * @param input
     * @return
     */
    public String mendHtmlChars(String input) {

        input = input.replace("amp;", "");

        input = input.replace("&#224;", "à");
        input = input.replace("&#228;", "ä");
        input = input.replace("&#196;", "Ä");
        input = input.replace("&#252;", "ü");
        input = input.replace("&#220;", "Ü");
        input = input.replace("&#246;", "ö");
        input = input.replace("&#214;", "Ö");

        input = input.replace("&#8222;", "\"");
        input = input.replace("&#8220;", "\"");
        input = input.replace("&quot;", "\"");

        input = input.replace("&#160;", " ");

        input = input.replace("&lt;", "<");
        input = input.replace("&gt;", ">");
        input = input.replace("&#8211;", "-");


        input = input.replace("&#91;", "<"); // This is [ , however replace with < to avoid Confuence keyword collision.
        input = input.replace("&#93;", ">"); // This is ] , reasoning as above.


        input = input.replace("<ins>", "");
        input = input.replace("</ins>", "");


        return input;
    }





    public String unpackTag(String input, String tag){
        String output = input.trim();
        String startTag = "<"+tag+">";
        String endTag = "</"+tag+">";
        String payload = null;
        if (output.startsWith(startTag) && output.endsWith(endTag)){
            int startTagEnd = startTag.length();
            int endTagStart = output.length() - endTag.length();
            payload = output.substring(startTagEnd, endTagStart);
            payload = payload.trim();
        }else return null;






        return payload;
    }



    public String removeAll(String text, String[] entries){

        for (String entry :entries){
            text = text.replace(entry, "");
        }


        return text;
    }


}
