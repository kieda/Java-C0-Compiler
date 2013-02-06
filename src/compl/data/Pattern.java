///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package compl.data;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @description holy regex, batman!
// * @author kieda
// */
////we search the next node before we search down.
//public class Pattern{
//    public class Node{
//        private String tab(int count){
//            String s = "";for(int i = 0; i < count; i++){
//                s+="    ";
//            }return s;
//        }
//        public String toString(){
//            String s = "";
////            String t = tab(tabs);
////            s+=t;
//            if(getAttribute(NOT)==ON) s+= "not{";
//            s+="\""+sequence+"\"";
////            if(getAttribute(BEGINRANGE)!=null){
////                int frum = getAttribute(BEGINRANGE);
////                int to = getAttribute(ENDRANGE);
////                s+= "; range : " + frum + ((to==MORE)?" or more":(" to "+to))+ " ";
////            }
//            if(getAttribute(INCLUDE)==OFF) s+= " (do not include)";
//            //t = tab(tabs+1);
//            if(sequences != null){
//                for(Node nn : sequences.values())
//                    s+= "["+nn.toString() + "]";
//            }
//            if(getAttribute(NOT)==ON) s+= "}";
//            if(next != null){
//                s+= " next:("+next.toString() + ")\n"; 
//            }
//            //t = tab(tabs+1);
//            
//            
//            return s;
//        }
//        private Node(){}
//        private Node(Character sequence){
//            this.sequence = sequence;
//            sequences = new HashMap<Character, Node>();
//            //sequences.add(this);
//            attributes.put(NOT, OFF);//we should not be in not yet
//            attributes.put(INCLUDE, ON);
//        }
//        private Character sequence;
//        private Node next;
//
//        private Map<Character, Node> sequences;//alternatives
//
//        private Map<Integer, Integer> attributes = new HashMap<Integer, Integer>();
//
//
//       /**
//        * sets a certain attribute in this node's list of attributes to ON.
//        * current attributes that can be set ON or OFF:
//        * 
//        *     INCLUDE    on by default     if off, this node's String 
//        *                                  sequence is not included in the
//        *                                  final string sequence. If this
//        *                                  Node is the last Node in a 
//        *                                  process of nodes, we rewind back
//        *                                  the length of the string.
//        *                                  
//        *                                  if on, this node's String 
//        *                                  sequence is included in the 
//        *                                  final returned token, and the 
//        *                                  scanner continues as usual.
//        * 
//        *     NOT        off by default    if on, the possibilities for 
//        *                                  what this step in a process 
//        *                                  characters are negated. For 
//        *                                  example, if a Node's string is
//        *                                  "hams", and this Node does not
//        *                                  have any associated Nodes 
//        *                                  'OR'-ed with this one, the 
//        *                                  process will continue only if 
//        *                                  the next sequence is NOT "hams".
//        *                                  In the case where a Node is 
//        *                                  'OR'-ed with other nodes, say
//        *                                  "good" OR "hams", the process 
//        *                                  will continue only if the next 
//        *                                  string sequence is 
//        *                                  NOT("good" or "hams"), or not
//        *                                  "good" and not "hams".
//        *                                  Does not negate a Node's 
//        *                                  children or surrounding Nodes in 
//        *                                  a process.
//        * 
//        *                                  if off, this node acts as usual
//        *                                  and is not negated.
//        */
//        public static final int ON = 1;
//       /**
//        * sets an attribute off. See ON.
//        */
//        public static final int OFF = 0;
//
//       /**
//        * attribute NOT:
//        * possible values: OFF, ON.
//        * 
//        *     NOT        OFF by default    if ON, the possibilities for 
//        *                                  what this step in a process 
//        *                                  characters are negated. For 
//        *                                  example, if a Node's string is
//        *                                  "hams", and this Node does not
//        *                                  have any associated Nodes 
//        *                                  'OR'-ed with this one, the 
//        *                                  process will continue only if 
//        *                                  the next sequence is NOT "hams".
//        *                                  In the case where a Node is 
//        *                                  'OR'-ed with other nodes, say
//        *                                  "good" OR "hams", the process 
//        *                                  will continue only if the next 
//        *                                  string sequence is 
//        *                                  NOT("good" or "hams"), or not
//        *                                  "good" and not "hams".
//        *                                  Does not negate a Node's 
//        *                                  children or surrounding Nodes in 
//        *                                  a process.
//        * 
//        *                                  if OFF, this node acts as usual
//        *                                  and is not negated.
//        */
//        public static final int NOT = 100;
//
//
//       /**
//        * attribute BEGINRANGE:
//        * possible values: BEGINRANGE >=0
//        * 
//        * Used with ENDRANGE, such that the number of characters matched 
//        * from the string sequence is on the range (BEGINRANGE, ENDRANGE). 
//        * BEGINRANGE must be less than ENDRANGE.
//        * 
//        * 
//        * 
//        * Only adds the string values matched in the range to the final 
//        * token.
//        * 
//        * The value MORE represents that we continue matching until we do 
//        * not have a match (equivalent to range n,infinity).
//        * 
//        * for example, finding "*" on the range 0,10 on the string
//        * "*** hi**"
//        * will return "***". If it were on the range 4, 10 on the same 
//        * string, there would not be a match, as the character sequence 
//        * "***" is too short. Alternatively, if it were on the range 0,2,
//        * "**" would be returned. If called again, "*" would be returned.
//        */
//        public static final int BEGINRANGE = 200;
//       /**
//        * attribute ENDRANGE:
//        * possible values: MORE, (ENDRANGE >=0 and ENDRANGE > BEGINRANGE)
//        * 
//        * Used with ENDRANGE, such that the number of characters matched 
//        * from the string sequence is on the range (BEGINRANGE, ENDRANGE). 
//        * BEGINRANGE must be less than ENDRANGE.
//        * 
//        * 
//        * 
//        * Only adds the string values matched in the range to the final 
//        * token.
//        * 
//        * The value MORE represents that we continue matching until we do 
//        * not have a match (equivalent to range n,infinity).
//        * 
//        * for example, finding "*" on the range 0,10 on the string
//        * "*** hi**"
//        * will return "***". If it were on the range 4, 10 on the same 
//        * string, there would not be a match, as the character sequence 
//        * "***" is too short. Alternatively, if it were on the range 0,2,
//        * "**" would be returned. If called again, "*" would be returned.
//        */
//        public static final int ENDRANGE = 201;
//
//
//       /**
//        * represents a value that is applied to ENDRANGE, representing an
//        * unbounded range.
//        */
//        public static final int MORE = -1;
//
//       /**
//        * should we include whatever we match on this node into the 
//        * returned char sequence? If this is off, then we won't include the
//        * char sequence at this node into the final returned token. if this
//        * include is at the end (the very last token; or if next is null),
//        * we rewind the search the last number of chars in the last node's 
//        * charsequence. Recursively applied to all sub-nodes.
//        */
//        public static final int INCLUDE = 151;
//
//       /**
//        * returns the integer mapping to a given attribute, or null if the
//        * mapping does not exist
//        */
//        public Integer getAttribute(int key){
//            return attributes.get(key);
//        }
//
//       /**
//        * sets a certain attribute for this node. Throws an error if the
//        * key is not recognizable, or if the value for the given key
//        * is invalid.
//        */
//        public Integer setAttribute(int key, int value){
//            assert (key==NOT)? (value==ON)||(value==OFF)
//                        :(key==BEGINRANGE)?(value==MORE)||(value>=0)
//                        :(key==ENDRANGE)?(value==MORE)||(value>=0)
//                        :(key==INCLUDE)?(value==ON)||(value==OFF)
//                        :false :
//                    "invalid key/value combination: "+key + "/"+value;
//            return attributes.put(key, value);
//        } 
//        int matches = 0;
//        
//
//       /**
//        * sets the range for matching a char sequence. By default, we match
//        * this node's char sequence exactly once.
//        */
//        //strings of 0 characters or more is 
//        //range(0, MORE)
//        //strings of length 1 through 5 
//        //range(1, 5)
//        public Node range(int from, int to){
//            assert from <= to ||to == MORE;
//            assert from >= 0;
//            attributes.put(BEGINRANGE, from);
//            attributes.put(ENDRANGE, to);
//            return this;
//        }
//        public Node rangeChildren(int from, int to){
//            assert from <= to ||to == MORE;
//            assert from >= 0;
//            attributes.put(BEGINRANGE, from);
//            attributes.put(ENDRANGE, to);
//            if(sequences!=null)
//                for(Node n : sequences.values()){
//                    n.rangeChildren(from, to);
//                }
//            return this;
//        }
//        public Node or(Node p){
//            assert (p != null) && p.sequence != null;
//            sequences.put(p.sequence, p);
//            return this;
//        }
//        public Node or(Pattern p){
//            assert (p != null) && p.root != null;
//            sequences.put(p.root.sequence, p.root);
//            return this;
//        }
//        public Node or(char seq){
//            sequences.put(seq, new Node(seq));
//            return this;
//        }
//
//        /**puts the node in the "or", then traverses down the tree structure**/
//        public Node orDown(Node p){
//            assert (p != null) && p.sequence != null;
//            sequences.put(p.sequence, p);
//            return p;
//        }
//        public Node orDown(Pattern p){
//            assert (p != null) && p.root != null;
//            sequences.put(p.root.sequence, p.root);
//            return p.root;
//        }
//        public Node orDown(char seq){
//            Node n = new Node(seq);
//            sequences.put(seq, n);
//            return n;
//        }
//
//       /**
//        * sets the next node on just this node as this node.
//        */
//        public Node then(char seq){
//            next = new Node(seq);
//            return next;
//        }
//        public Node then(Pattern p){
//            next = p.root;
//            return next;
//        }
//        public Node then(Node n){
//            next = n;
//            return next;
//        }
//
//       /**
//        * recursively applies the next node to all sub-nodes
//        */
//        /*public Node thenAll(char seq){
//            return thenAll(new Node(seq));
//        }
//        public Node thenAll(Pattern p){
//            return thenAll(p.root);
//        }
//        public Node thenAll(Node n){
//            next = n;
//            if(sequences != null){
//                for(Node nex :sequences.values())
//                    nex.thenAll(next);
//            }
//            return next;
//        } */
//       /**
//        * recursively applies the next node to some sub-nodes
//        */
//        /*public Node thenSome(char seq){
//            return thenSome(new Node(seq));
//        }
//        public Node thenSome(Pattern p){
//            return thenSome(p.root);
//        }
//        public Node thenSome(Node n){
//            if(next== null)
//                next = n;
//            else
//                next.thenSome(n);
//            if(sequences != null){
//                for(Node nex :sequences.values())
//                    nex.thenSome(n);
//            }
//            return next;
//        }*/
//        //can turn back on by calling this twice
//        public Node not(){
//            //not recursively applied. doing so would be a disaster.
//            if(attributes.get(NOT) == ON) attributes.put(NOT, OFF);
//            else
//                attributes.put(NOT, ON);
//            return this;
//        }
//        //public Pattern and(){return this;}
//
//        private void notIncl(){
//            attributes.put(INCLUDE, OFF);
//            if(sequences != null){
//                for(Node child : sequences.values()){
//                    child.notInclude();
//                }
//            }
//        }
//        public Node notInclude(){
//            notIncl();
//            return this;
//        }
//        public Pattern end(){
//            return get();
//        }
//    }
//    private Pattern get(){return this;}
//    private Node root;
//    private Pattern(){}
//    private Pattern(boolean b){
//        if(b)root = new Node(null);
//        else root = new Node();
//    }
//    private Pattern(char seq){
//        root = new Node(seq);
//    }
//    //for comments: Pattern.create("/*").then(Pattern.create(ANY).range(0, N)).then("*/");
//    //so, it's /*, then a string of any characters greater than or equal to 0 characters long, then */
//    public static final Pattern HEX_NUMBER;
//    //a number that is just zero, and is recognized as just zero. the token
//    //is 0, then follwed by something that is not x and not a decimal
//    
//    //a decimal one through nine
//    public static final Pattern DECIMAL_1_9;
//    public static final Pattern DECIMAL;
//    public static final Pattern HEX;
//    public static final Pattern X_ANYCASE;
//    public static final Pattern ZERO;
//    public static final Pattern DECIMAL_NUMBER;
//    public static final Pattern WHITE_SPACE;
//    public static final Pattern NON_WHITE_SPACE;
//    public static final Pattern DIVIDE;
//    public static final Pattern BLOCK_COMMENT;
//        //a block comment is a /*, then a length of characters that are not 
//        //*/, then the characters */.
//    public static final Pattern LINE_COMMENT;
//    static{
//        Node n = Pattern.create('1').
//            or('2').or('3').or('4').or('5').
//            or('6').or('7').or('8').or('9');
//        DECIMAL_1_9 = n.end();
//    }static{
//        Node n = DECIMAL_1_9.copyRoot().or('0');
//        DECIMAL = n.end();
//    }static{
//        Node n =DECIMAL.copyRoot().
//            or('A').or('B').or('C').or('D').or('E').or('F').
//            or('a').or('b').or('c').or('d').or('e').or('f'); 
//         HEX = n.end();
//    }static{
//         Node n = Pattern.create('x').or('X');
//         X_ANYCASE = n.end();
//    }static{
//        Node n = Pattern.create('0'); n.then(X_ANYCASE.copyRoot()).thenAll(HEX.copyRoot().range(0, Node.MORE));
//        HEX_NUMBER = n.end();
//    } static{
//        Node n = Pattern.create('0');
//        n.then(Pattern.create(X_ANYCASE).or(DECIMAL.copyRoot()).not().notInclude());
//        ZERO = n.end();
//    } static{
//        Node n = ZERO.copyRoot();
//        n.or(DECIMAL_1_9.copyRoot()); n.or(Pattern.create('0').then(DECIMAL.copyRoot())).thenSome(DECIMAL.copyRoot().range(0, Node.MORE));
//        DECIMAL_NUMBER = n.end();
//        //we start off with a number 1-9, or 0. If 0, the next char must be 0-9 
//        //(as to not mistake 0x with a decimal number). If the next char was 1-9, 
//        //then we may proceed to analyze till we reach something that is not a decimal.
//        //The only case not covered from what I said above is if 0 is alone. This is covered
//        //by or-ing it with what we know will just be zero.
//    } static{
//         Node n = Pattern.create(' ').or('\t').or('\n').or('\r');
//         WHITE_SPACE = n.end();
//    } static{
//         Node n = WHITE_SPACE.copyRoot().not();
//         NON_WHITE_SPACE = n.end();
//    } static{
//        Node n  = Pattern.create('/');
//        n.then(Pattern.create('*').or('/').not().notInclude());
//        DIVIDE = n.end();
//    } static{
//        Node n = Pattern.create('/');
//        Node nnot = Pattern.create('*');nnot.then('/');nnot.not();nnot.range(0, Node.MORE);
//        n.then('*').then(nnot).then('*').then('/');
//        BLOCK_COMMENT = n.end();
//    } static{
//        Node n = Pattern.create('/');n.then('/').then(Pattern.create('\n').not().range(0, Node.MORE)).then('\n');
//        LINE_COMMENT = n.end();
//    }
//    
//    
//    private static boolean validChar(char c, Node n){
//        if(n.sequence==c) return true;
//        if(n.sequences != null){
//            return n.sequences.containsKey(c);
//        }
//        return false;
//    }
//    private static boolean valid(String s, Node n){
//        boolean not = n.getAttribute(Node.NOT)==Node.OFF;
//        Integer ran = n.getAttribute(Node.BEGINRANGE);
//        if(ran != null){
//            int beg = ran;
//            int end = n.getAttribute(Node.ENDRANGE);
//            
//        } 
//        if(sequence != null){
//            char c = s.charAt(0);
//            if(sequence == c){
//                String j = s.substring(1, s.length());
//                if(j.isEmpty()) 
//                    return not?next == null:next != null;
//                if(next != null){
//                    System.out.println(j + " " +next);
//                    boolean b = next.valid(j);
//                    if(b==true) return not;
//                }
//            }
//            if(sequences != null){
//                //terrible at the moment
//                Node n = sequences.get(c);
//                if(n != null){
//                    String j = s.substring(1, s.length());
//
//                    if(j.isEmpty()) return not?next == null:next != null;
//                    System.out.println(s + " " +n);
//                    return n.valid(s);
//                }
//            }
//            return !not;
//        }
//        return true;
//    }
//    
//        
//    
//    
//    
//    public static void print(Pattern n){
//        System.out.println(n.root);
//    }
//    public static Node create(Pattern p){
//        Pattern n = new Pattern();
//        n.root = p.copyRoot();
//        return n.root;
//    }
//    public static Node or(char seq){
//        Node root = new Pattern(true).root;
//        return root.or(seq);
//    }
//    public static Node create(char seq){return new Pattern(seq).root;}
//    public static Node create(){return new Pattern(true).root;}
//
//    private static Pattern copy(Pattern pp){
//        if(pp == null) return null;
//        Pattern p = new Pattern();
//        if(pp.root != null) p.root = pp.copyRoot();
//        return p;
//    }
//    private static Node copyNode(Node node){
//        if(node==null)return null;
//        Node n = new Pattern(false).root;
//        if(node.sequence != null) n.sequence = new Character(node.sequence);
//        
//        //copy char sequence
//        if(node.attributes != null){
//            for(Map.Entry<Integer, Integer> me :node.attributes.entrySet())
//                n.attributes.put(me.getKey(), me.getValue());
//        }//copy attribs
//
//
//        if(node.sequences != null){
//            n.sequences = new HashMap<Character, Node>();
//            for(Node no : node.sequences.values())
//                n.sequences.put(new Character(no.sequence), copyNode(no));//recursive copying
//        }//copy alternatives
//
//        if(node.next != null){
//            n.next = copyNode(node.next);//recursive copying
//        }
//
//        return n;
//    }
//    private static Node copyRoot(Pattern pp){
//        if(pp == null) return null;
//        return copyNode(pp.root);
//    }
//    public Node copyRoot(){
//        return copyRoot(this);
//    }
//    public Pattern copy(){
//        return copy(this);
//    }
//    public boolean valid(String s){
//        //return root.valid(s);
//        return valid(s, root);
//    }
//}
