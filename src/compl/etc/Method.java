/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
/**
 * a wrapper for a complete method.
 * Includes interesting shit like expressions, syntax, relations, etc...
 * Because the 
 * @author kieda
 */
//a method in itself is one block of syntax.
public class Method extends Syntax{
    
    
    interface RelationName{
        //the name the operation goes by. (i.e. division)
        String relationAlias();
    }
    
    //an element that can be evaluated in an expression. i.e. a relation 
    //or a Data type. X and D have to both be expression elements, so we can 
    //chain together expressions.
    interface ExpressionElement<X extends compl.etc.Method.ExpressionElement, D extends compl.etc.Method.ExpressionElement>{
        /**
         * returns the number of arguments that this function/operation takes.
         * 
         * must be a Natural number.
         */
        public int numArgs();
        
        /**
         * define x as some list consisting of types of readable data 
         * ("expression elements").
         * The first element of x, X1, corresponds to the first set of inputs 
         * that the function could take. The labeling continues for the second 
         * element, X2; till then^th element where n is equal to the numArgs() 
         * in the Naturals.
         * this function, "relate", gives the set of outputs possible that this 
         * operation will have for the given parameters x[] such that
         *      f : X1×X2×...×Xn → D
         * where D is the set of outputs. If the parameter types do not match
         * your requirement (or where the set is not defined) null is returned.
         * 
         * The set types are left open; so you could have an actual set of 
         * numbers f : {1,2,3} → {3,4,5}
         * 
         * or you could define types
         *      f : REALS → NATURALS
         * 
         * for example, the relation logical equals "==" would look like
         *      == : INT, INT         → BOOLEAN
         * or   == : CHAR, CHAR       → BOOLEAN
         * or   == : BOOLEAN, BOOLEAN → BOOLEAN
         * 
         * another example, for the implementation of something like addition,
         * we could make x[] be of type int[]. Then, we could have
         *      + : int, int → int
         * such that
         *      D = (X1 + X2)
         * 
         */
        public D relate(X[] x);
    }
    interface OperationOrder extends Comparable<compl.etc.Method.OperationOrder>{
        /*highest priority is zero*/
        
        int PRIORITY_0  = 0;
        int PRIORITY_1  = 1;
        int PRIORITY_2  = 2;
        int PRIORITY_3  = 3;
        int PRIORITY_4  = 4;
        int PRIORITY_5  = 5;
        int PRIORITY_6  = 6;
        int PRIORITY_7  = 7;
        int PRIORITY_8  = 8;
        int PRIORITY_9  = 9;
        int PRIORITY_10 = 10;
        int PRIORITY_11 = 11;
        int PRIORITY_12 = 12;
        int PRIORITY_13 = 13;
        
        /*for C0, must be between 0 and 13 (inclusive)*/
        /*based on C0 reference for operation order*/
        int priority();
    }
    //f : R1 → R2
    //we want R1 and R2 to be sub-classes of Expression Elements, such that both
    //of the elements in the relation (the domain and the codomain) to be parts 
    //of an expression
    abstract class ExpressionRelation<R2 extends compl.etc.Method.ExpressionElement, R> 
        implements compl.etc.Method.ExpressionElement<compl.etc.Method.ExpressionElement, R2>, compl.etc.Method.OperationOrder
    {
        //possibly the relation's String (i.e. / is the identifier relation of division)
//        public abstract <D> D relationIdentifier();
        public abstract String[] relationIdentifier();
        abstract HashMap<R, R2> relation();
            //some arbitrary type R which can be used. Could be a tuple.
        
        /**
         * returns a positive number iff this priority is greater than o's priority
         * returns a negative number iff this priority is less than o's priority
         * returns 0 iff priority is equal to o's priority
         */
        public int compareTo(compl.etc.Method.OperationOrder o){
            //additive inverse. (the one with the closest number to zero is a 
            //higher priority)
            return o.priority() - priority();
        }
    }
    //A is simply used to define the domain, and B is used to describe the codomain.
    abstract class BinaryRelation<A extends compl.etc.Method.ExpressionElement, B extends compl.etc.Method.ExpressionElement,
            X extends compl.etc.Method.ExpressionElement> extends compl.etc.Method.ExpressionRelation<B, X>{
        abstract HashMap<X, B> relation();
        abstract HashSet<A> A();//left hand side
        abstract HashSet<B> B();//right hand side
        @Override public String toString(){
            return relationIdentifier() + " : " + A() + " → " + B();
        }
        public int numArgs(){
            return 1;
        }
        public B relate(compl.etc.Method.ExpressionElement[] x){
            assert x != null;
            assert x.length == numArgs();
            if(!A().contains(x[0])) return null;//in the set of things that are A
            return relation().get(x[0]);
            //will have the zero'th element, previous assertion
        }
    }
    
    //f : A×B → C
    abstract class TernaryRelation<A extends compl.etc.Method.ExpressionElement, B extends compl.etc.Method.ExpressionElement, C extends compl.etc.Method.ExpressionElement> 
        extends compl.etc.Method.ExpressionRelation<C, compl.etc.Method.TernaryRelation<A, B, C>.Pair>{
        //sub-class of syntax, as relations only exist in syntax
        
        
        
        //BAD. how about the set (A → B)→C? Where A is a LHS mapping. By the 
        //way C0 works (and most coding in general) C's output will always be 
        //known if A and B are known.
        
       class Pair {
            public Pair(A a, B b){
                this.a = a; this.b = b;
            }
            
            final A a;final B b;
        }
        //for binary, we have the domain, the codomain, and the relation.
        
        
        //domain
        abstract HashSet<A> A();//left hand side
        abstract HashSet<B> B();//right hand side
        //codomain
        abstract HashSet<C> C();//return val; may be void
        
        abstract HashMap<Pair, C> relation();
        
        public int numArgs(){
            return 2;
        }
        public C relate(ExpressionElement[] x){
            //left hand side, right hand side
            assert x != null;
            assert x[0] != null && x[1] != null;
            assert x.length == numArgs();
            if(A().contains(x[0])&& B().contains(x[1]))
                return relation().get(new Pair((A)x[0], (B)x[1]));
            return null;
        }
        
        @Override public String toString(){
            return relationIdentifier() + " : " + A() + "×" + B() + " → " + C();
        }
    }
    abstract class QuaternaryRelation<A extends compl.etc.Method.ExpressionElement, B extends compl.etc.Method.ExpressionElement, C extends compl.etc.Method.ExpressionElement, D extends compl.etc.Method.ExpressionElement> extends compl.etc.Method.ExpressionRelation<D, compl.etc.Method.QuaternaryRelation<A,B,C,D>.Tri>{
        //sub-class of syntax, as relations only exist in syntax
        
        
        
        //BAD. how about the set (A → B)→C? Where A is a LHS mapping. By the 
        //way C0 works (and most coding in general) C's output will always be 
        //known if A and B are known.
        
        
        class Tri{
            public Tri(A a, B b, C c){
                this.a = a; this.b = b;this.c = c;
            }
            
            final A a;final B b; final C c;
        }
        //for binary, we have the domain, the codomain, and the relation.
        
        
        //domain
        abstract HashSet<A> A();//left hand side
        abstract HashSet<B> B();//right hand side
        abstract HashSet<C> C();
        
        //codomain
        abstract HashSet<D> D();
        
        abstract HashMap<Tri, D> relation();
        
        public int numArgs(){
            return 3;
        }
        public D relate(compl.etc.Method.ExpressionElement[] x){
            //left hand side, right hand side
            assert x != null;
            assert x[0] != null && x[1] != null && x[2] != null;
            assert x.length == numArgs();
            if(A().contains(x[0])&& B().contains(x[1]) )
                return relation().get(new Tri((A)x[0], (B)x[1], (C)x[2]));
            return null;
        }
        
        @Override public String toString(){
            return relationIdentifier() + " : " + A() + "×" + B() + " → " + C();
        }
    }
    abstract class SemiSymmetricQuaternaryRelation<A extends compl.etc.Method.ExpressionElement, B extends compl.etc.Method.ExpressionElement, D extends compl.etc.Method.ExpressionElement> extends compl.etc.Method.QuaternaryRelation<A,B,B,D>{
        //sub-class of syntax, as relations only exist in syntax
        //for binary, we have the domain, the codomain, and the relation.
        
        
        
        HashSet<B> C(){ return B();}
        
        class SymTri extends Tri{
            public SymTri(A a, B b){
                super(a, b, b);
            }
        }
        
        public D relate(compl.etc.Method.ExpressionElement[] x){
            //left hand side, right hand side
            assert x != null;
            assert x[0] != null && x[1] != null && x[2] != null;
            assert x.length == numArgs();
            if(A().contains(x[0])&& B().contains(x[1]) )
                return relation().get(new Tri((A)x[0], (B)x[1], (B)x[2]));
            return null;
        }
        
        @Override public String toString(){
            return relationIdentifier() + " : " + A() + "×" + B() + " → " + C();
        }
    }
    abstract class SymmetricRelation3<A extends ExpressionElement, C extends ExpressionElement> extends TernaryRelation<A, A, C>{
        class SymmetricPair extends Pair{
            public SymmetricPair(A a){
                super(a, a);
            }
        }
        
        //you only have to fill in B()
        @Override HashSet<A> A() {return B();};
    }
    abstract class SymmetricRelation2<A extends ExpressionElement, X extends ExpressionElement> extends BinaryRelation<A, A, X>{
        @Override HashSet<A> A() {return B();};
    }
    abstract class SingleRelation3<A  extends ExpressionElement> extends SymmetricRelation3<A, A>{
        @Override HashSet<A> A() {return B();}
        @Override HashSet<A> B() {return C();}
    }
    //a relation between C0 datas
    abstract class C0Relation3 extends TernaryRelation<C0Data, C0Data, C0Data> implements RelationName{}
    abstract class C0Relation2 extends BinaryRelation<C0Data, C0Data, C0Data> implements RelationName{}
    abstract class C0SymmetricRelation4 extends SemiSymmetricQuaternaryRelation<C0Data, C0Data, C0Data> implements RelationName{}
    abstract class C0SymmetricRelation3 extends SymmetricRelation3<C0Data, C0Data>{}
    abstract class C0SymmetricRelation2 extends SymmetricRelation2<C0Data, C0Data>{}
    abstract class C0SingleRelation3 extends SingleRelation3<C0Data>{}
    abstract class C0SameTypeOperation3 extends C0SingleRelation3 implements RelationName{
        final HashSet<C0Data> SET;
        final HashMap<Pair, C0Data> RELATION;
        public C0SameTypeOperation3(C0Data type){
            C0Data i = type;
            HashSet<C0Data> set = new HashSet<C0Data>();set.add(i);
            HashMap<Pair, C0Data> relation = new HashMap<Pair, C0Data>();
            relation.put(new SymmetricPair(i), i);
            SET = set;
            RELATION = relation;
        }
        @Override HashSet<C0Data> C() {
            return SET;
        }@Override HashMap<Pair, C0Data> relation() {
            return RELATION;
        }
    }
    abstract class C0SameTypeOperation2 extends C0SymmetricRelation2 implements RelationName{
        final HashSet<C0Data> SET;
        final HashMap<C0Data, C0Data> RELATION;
        public C0SameTypeOperation2(C0Data type){
            C0Data i = type;
            HashSet<C0Data> set = new HashSet<C0Data>();set.add(i);
            HashMap<C0Data, C0Data> relation = new HashMap<C0Data, C0Data>();
            relation.put(i, i);
            SET = set;
            RELATION = relation;
        }
        @Override HashSet<C0Data> B() {
            return SET;
        }@Override HashMap<C0Data, C0Data> relation() {
            return RELATION;
        }
    }
    /**a C0 integer operation. Like a division, multiplication, and bitwise operations**/
    abstract class C0IntegerOperation3 extends C0SameTypeOperation3{
        public C0IntegerOperation3(){super(Types.INT);}
    }
    abstract class C0IntegerOperation2 extends C0SameTypeOperation2{
        public C0IntegerOperation2(){super(Types.INT);}
    }
    abstract class C0BooleanOperation2 extends C0SameTypeOperation2{
        public C0BooleanOperation2(){super(Types.BOOL);}
    }
    class NOT extends C0BooleanOperation2{
        final String[] IDENTITY = new String[]{"!"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "not";
        } @Override public int priority() {
            return PRIORITY_1;
        }
    }
    //integer operations
    class Division extends C0IntegerOperation3{
        final String[] IDENTITY = new String[]{"/"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "division";
        } @Override public int priority() {
            return PRIORITY_2;
        }
    } 
    class Multiplication extends C0IntegerOperation3{
        final String[] IDENTITY = new String[]{"*"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        }
        @Override public String relationAlias() {
            return "multiplication";
        } @Override public int priority() {
            return PRIORITY_2;
        }
    }
    class Addition extends C0IntegerOperation3{
        final String[] IDENTITY = new String[]{"+"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        }
        @Override public String relationAlias() {
            return "addition";
        } @Override public int priority() {
            return PRIORITY_3;
        }
    } 
    class Subtraction extends C0IntegerOperation3{
        final String[] IDENTITY = new String[]{"-"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        }
        @Override public String relationAlias() {
            return "subtraction";
        } @Override public int priority() {
            return PRIORITY_3;
        }
    }
    
    class BitwiseAND extends C0IntegerOperation3{
        final String[] IDENTITY = new String[]{"&"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        }
        @Override public String relationAlias() {
            return "bitwise and";
        } @Override public int priority() {
            return PRIORITY_7;
        }
    } 
    class BitwiseOR extends C0IntegerOperation3{
        final String[] IDENTITY = new String[]{"|"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        }
        @Override public String relationAlias() {
            return "bitwise or";
        } @Override public int priority() {
            return PRIORITY_9;
        }
    } 
    class BitwiseNOT extends C0IntegerOperation2{
        final String[] IDENTITY = new String[]{"~"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;//should be a binary relation
        }
        @Override public String relationAlias() {
            return "bitwise not";
        } @Override public int priority() {
            return PRIORITY_1;
        }
    } 
    class BitwiseXOR extends C0IntegerOperation3{
        final String[] IDENTITY = new String[]{"^"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        }
        @Override public String relationAlias() {
            return "bitwise xor";
        } @Override public int priority() {
            return PRIORITY_8;
        }
    }
    
    class BitwiseShiftL extends C0IntegerOperation3{
        final String[] IDENTITY = new String[]{"<<"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        }
        @Override public String relationAlias() {
            return "bitwise shift left";
        } @Override public int priority() {
            return PRIORITY_4;
        }
    }
    class BitwiseShiftR extends C0IntegerOperation3{
        final String[] IDENTITY = new String[]{">>"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        }
        @Override public String relationAlias() {
            return "bitwise shift right";
        } @Override public int priority() {
            return PRIORITY_4;
        }
    }
    class Mod extends C0IntegerOperation3{
        final String[] IDENTITY = new String[]{"%"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        }
        @Override public String relationAlias() {
            return "modulo";
        } @Override public int priority() {
            return PRIORITY_2;
        }
    }
    
    //used in comparisons (just shortcuts, and so we don't have to make a 
    //million) arrays
    //no such things as string operations and char operations
    abstract class Comparison extends C0SymmetricRelation3 implements RelationName{
        HashSet<C0Data> B(){return null;}
        HashSet<C0Data> C(){return null;}
        HashMap<Pair, C0Data> relation(){return null;}
    }
    abstract class ComparisonEquality extends Comparison{
        public C0Data relate(ExpressionElement[] x){
            //left hand side, right hand side
            assert x != null;
            assert x.length == numArgs();
            assert x[0] != null && x[1] != null;
            
            if(!(x[0] instanceof C0Data)){ 
                return null;
            }if(!(x[1] instanceof C0Data)){ 
                return null;
            } if(!((C0Data)x[0]).equals(x[1])){
                return null;
            } if(!( x[0] instanceof Int ||
                    x[0] instanceof Char||
                    x[0] instanceof Bool||
                    x[0] instanceof Point||
                    x[0] instanceof Arra)) return null;
            return Types.BOOL;
        }
    }
    abstract class ComparisonComparable extends Comparison{
        public C0Data relate(ExpressionElement[] x){
            //left hand side, right hand side
            assert x != null;
            assert x.length == numArgs();
            assert x[0] != null && x[1] != null;
            
            if(!(x[0] instanceof C0Data)){ 
                return null;
            }if(!(x[1] instanceof C0Data)){ 
                return null;
            } if(!((C0Data)x[0]).equals(x[1])){
                return null;
            } if(!( x[0] instanceof Int || x[0] instanceof Char)) return null;
            return Types.BOOL;
        }
    }
    
    class EQ extends ComparisonEquality{
        final String[] IDENTITY = new String[]{"=="};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "equal to";
        } @Override public int priority() {
            return PRIORITY_6;
        }
    } 
    class NEQ extends ComparisonEquality{
        final String[] IDENTITY = new String[]{"!="};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "not equal to";
        } @Override public int priority() {
            return PRIORITY_6;
        }
    }
    class LT extends ComparisonComparable{
        final String[] IDENTITY = new String[]{"<"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "less than";
        } @Override public int priority() {
            return PRIORITY_5;
        }
    }
    class GT extends ComparisonComparable{
        final String[] IDENTITY = new String[]{">"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "greater than";
        } @Override public int priority() {
            return PRIORITY_5;
        }
    }
    class LE extends ComparisonComparable{
        final String[] IDENTITY = new String[]{"<="};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "less than or equal to";
        } @Override public int priority() {
            return PRIORITY_5;
        }
    }
    class GE extends ComparisonComparable{
        final String[] IDENTITY = new String[]{">="};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "greater than or equal to";
        } @Override public int priority() {
            return PRIORITY_5;
        }
    }
    
    //no such things as string operations and char operations. boolean operationsaer negations, etc
    abstract class C0BooleanOperation extends C0SameTypeOperation3{
        public C0BooleanOperation(){super(Types.BOOL);}
    }
    
    class OR extends C0BooleanOperation{
        final String[] IDENTITY = new String[]{"||"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        }
        @Override public String relationAlias() {
            return "or";
        }  @Override public int priority() {
            return PRIORITY_11;
        }
    }
    class AND extends C0BooleanOperation{
        final String[] IDENTITY = new String[]{"&&"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        }
        @Override public String relationAlias() {
            return "and";
        } @Override public int priority() {
            return PRIORITY_10;
        }
    }
    
    //todo: parentheses (as a relation)
    //it's a binary relation. the input is the evalutated input from the 
    //beginning of the parentheses till the end, and the output is the evaluated
    //type/value.
    
    //todo: equals.
    //it's a ternary relation where the left hand side is a C0Variable, the 
    //right hand side is a variable/value of the same type, and the return is 
    //void
    
    class PointDeref extends C0Relation2{
        final String[] IDENTITY = new String[]{"*"};
        //unnecessary for the task at hand.
        @Override HashMap<C0Data, C0Data> relation() {return null;}
        @Override HashSet<C0Data> A() {return null;}
        @Override HashSet<C0Data> B() {return null;}
        //overridden for the additional capabilities/knowledge of the input.
        public C0Data relate(ExpressionElement[] x){
            assert x != null;
            assert x.length == numArgs();
            if(!(x[0] instanceof Point)) return null;
            return ((Point)x[0]).content;
            //the content is going to be the type of the inner thing.
        }
        
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "pointer dereference";
        } @Override public int priority() {
            return PRIORITY_1;
        }
    }
    //in a binary relation, the operation _precedes_ the value, or
    //     (operation) (value)
    //like
    //     *pointer
    
    //in a ternary relation, the values are is before and after the operator,
    //or
    //    (value 1)(operation)(value 2)
    //like
    //     3 + 5
    
    //in a quaternary relation, there are two operators. The values are before 
    //the first operator, between the two operators, and after the second 
    //operator
    //or
    //    (value 1)(operation 1)(value 2)(operation 2)(value 3)
    //like
    //    true?3:5
    
    //todo: ->
    //    -> : C0Struct* → struct var
    class StringToken implements ExpressionElement{
        String token;
        @Override public int numArgs() {
            return 0;
        } @Override public ExpressionElement relate(ExpressionElement[] x) {
            return this;
        }
    }
    class FieldDeref extends C0Relation3{
        final String[] IDENTITY = new String[]{"->"};
        //unnecessary for the task at hand.
        //@Override HashMap<C0Data, C0Data> relation() {return null;}
        @Override HashSet<C0Data> A() {return null;}
        @Override HashSet<C0Data> B() {return null;}
        @Override HashSet<C0Data> C() {return null;}
        @Override HashMap<Pair, C0Data> relation() {return null;}
        //overridden for the additional capabilities/knowledge of the input.
        
        public C0Data relate(ExpressionElement[] x){
            assert x != null;
            assert x.length == numArgs();
            assert x[1] != null && x[0] != null;
            //it's a struct*, so we need to dereference the pointer first
            if(!(x[0] instanceof Point)) return null;
            C0Data in = ((Point)x[0]).content;
            if(!(in instanceof C0Struct_parsed)) return null;//mabye a log here?
            if(!(x[1] instanceof StringToken)) return null;
            String var_name = ((StringToken)x[1]).token;
            
            return ((C0Struct_parsed)in).getVariable(var_name);
            //the content is going to be the type of the inner thing.
        }
        
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "field dereference";
        } @Override public int priority() {
            return PRIORITY_0;
        }
    }
    class FieldSelect extends C0Relation3{
        final String[] IDENTITY = new String[]{"."};
        //unnecessary for the task at hand.
        //@Override HashMap<C0Data, C0Data> relation() {return null;}
        @Override HashSet<C0Data> A() {return null;}
        @Override HashSet<C0Data> B() {return null;}
        @Override HashSet<C0Data> C() {return null;}
        @Override HashMap<Pair, C0Data> relation() {return null;}
        //overridden for the additional capabilities/knowledge of the input.
        
        public C0Data relate(ExpressionElement[] x){
            assert x != null;
            assert x.length == numArgs();
            assert x[1] != null && x[0] != null;
            //it's a struct*, so we need to dereference the pointer first
            if(!(x[0] instanceof C0Struct_parsed)) return null;
            if(!(x[1] instanceof StringToken)) return null;
            String var_name = ((StringToken)x[1]).token;
            
            return ((C0Struct_parsed)x[0]).getVariable(var_name);
            //the content is going to be the type of the inner thing.
        }
        
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "field select";
        } @Override public int priority() {
            return PRIORITY_0;
        }
    }
    class ArrayBrackets extends C0Relation3{
        final String[] IDENTITY = new String[]{"[", "]"};
        //unnecessary for the task at hand.
        HashSet<C0Data> SET = new HashSet<C0Data>(Arrays.asList(Types.INT));
        @Override HashSet<C0Data> A() {return null;}
        @Override HashSet<C0Data> B() {return SET;}
        @Override HashSet<C0Data> C() {return null;}
        @Override HashMap<Pair, C0Data> relation() {return null;}
        //overridden for the additional capabilities/knowledge of the input.
        
        public C0Data relate(ExpressionElement[] x){
            assert x != null;
            assert x.length == numArgs();
            assert x[0] != null && x[1] != null;
            //it's a struct*, so we need to dereference the pointer first
            if(!(x[0] instanceof Arra)) return null;
            if(!(B().contains(x[1]))) return null;
            
            return ((Arra)x[0]).type;
            //the content is going to be the type of the inner thing.
        }
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "array subscript";
        } @Override public int priority() {
            return PRIORITY_0;
        }
    }
    //todo .
    //     . : C0Struct  → struct var
    abstract class EqualOp extends C0Relation3{
        HashSet<C0Data> C = new HashSet<C0Data>(Arrays.asList(Types.VOID));
        @Override HashSet<C0Data> A() {return null;}
        @Override HashSet<C0Data> B() {return null;}
        @Override HashSet<C0Data> C() {return C;}
        @Override HashMap<Pair, C0Data> relation() {return null;}
        @Override public int priority() {
            return PRIORITY_13;
        }
    }
    abstract class IncrOp extends C0Relation2{
        HashSet<C0Data> B = new HashSet<C0Data>(Arrays.asList(Types.VOID));
        @Override HashSet<C0Data> A() {return null;}
        @Override HashSet<C0Data> B() {return B;}
        @Override HashMap<C0Data, C0Data> relation() {return null;}
        @Override public int priority() {
            return PRIORITY_1;
        }
        public C0Data relate(ExpressionElement[] x){
            assert x != null;
            assert x.length == numArgs();//1
            assert x[0] != null;
            if(!(x[0] instanceof IntVar)) return null;
            return Types.VOID;
        }
    }
    class Incr extends IncrOp{
        final String[] IDENTITY = new String[]{"++"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "increment";
        }
    }
    class Decr extends IncrOp{
        final String[] IDENTITY = new String[]{"--"};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "decrement";
        }
    }
    
    class Equals extends EqualOp{
        final String[] IDENTITY = new String[]{"="};
        public C0Data relate(ExpressionElement[] x){
            assert x != null;
            assert x.length == numArgs();
            assert x[0] != null && x[1] != null;
            //it's a struct*, so we need to dereference the pointer first
            if(!(x[0] instanceof C0Variable)) return null;
            C0Data type = (C0Variable)x[0];
            if(!(x[1] instanceof C0Data)) return null;
            if(!((C0Data)x[1]).equals(type)) return null;
            
            return Types.VOID;
            //the content is going to be the type of the inner thing.
        }
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "equals";
        }
    }
    abstract class IntEquals extends EqualOp{
        public C0Data relate(ExpressionElement[] x){
            assert x != null;
            assert x.length == numArgs();
            assert x[0] != null && x[1] != null;
            //it's a struct*, so we need to dereference the pointer first
            if(!(x[0] instanceof IntVar)) return null;
            C0Data type = (IntVar)x[0];
            if(!(x[1] instanceof C0Data)) return null;
            if(!((C0Data)x[1]).equals(type)) return null;
            return Types.VOID;
            //the content is going to be the type of the inner thing.
        }
    }
    class PlusEquals extends IntEquals{
        final String[] IDENTITY = new String[]{"+="};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "plus equals";
        }
    }
    class MinusEquals extends IntEquals{
        final String[] IDENTITY = new String[]{"-="};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "minus equals";
        }
    }
    class TimesEquals extends IntEquals{
        final String[] IDENTITY = new String[]{"*="};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "times equals";
        }
    }
    class DivideEquals extends IntEquals{
        final String[] IDENTITY = new String[]{"/="};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "divide equals";
        }
    }
    class ModEquals extends IntEquals{
        final String[] IDENTITY = new String[]{"%="};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "mod equals";
        }
    }
    class BitAndEquals extends IntEquals{
        final String[] IDENTITY = new String[]{"&="};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "bitwise and equals";
        }
    }
    class BitXOrEquals extends IntEquals{
        final String[] IDENTITY = new String[]{"^="};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "bitwise xor equals";
        }
    }
    class BitOrEquals extends IntEquals{
        final String[] IDENTITY = new String[]{"|="};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "botwise or equals";
        }
    }
    class BitShLEquals extends IntEquals{
        final String[] IDENTITY = new String[]{"<<="};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "bitwise shift left equals";
        }
    }
    class BitShREquals extends IntEquals{
        final String[] IDENTITY = new String[]{">>="};
        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public String relationAlias() {
            return "bitwise shift right equals";
        }
    }
    
    //todo ++
    //    ++ : var int → int
    //todo --
    //    -- : var int → int
    
    
    //todo +=, etc=
    
    //(quaternary)
    
    class Conditional extends C0SymmetricRelation4{
        final String[] IDENTITY = new String[]{"?", ":"};
        final HashSet<C0Data> A = new HashSet<C0Data>(Arrays.asList(Types.BOOL));
        @Override HashSet<C0Data> A() {return A;
        } @Override HashSet<C0Data> B() {return null;
        } @Override HashSet<C0Data> D() {return null;}

        @Override HashMap<Tri, C0Data> relation() {return null;}

        @Override public String[] relationIdentifier() {
            return IDENTITY;
        } @Override public int priority() {
            return PRIORITY_12;
        } @Override public String relationAlias() {
            return "conditional expression";
        }
        
        public C0Data relate(ExpressionElement[] x){
            //left hand side, right hand side
            assert x != null;
            assert x.length == numArgs();
            assert x[0] != null && x[1] != null && x[2] != null;
            
            if(!(x[0] instanceof Bool)){ 
                return null;
            }if(!(x[1] instanceof C0Data)){ 
                return null; 
            }if(!(x[2] instanceof C0Data)){ 
                return null;
            }if(!((C0Data)x[1]).equals(x[2])){
                return null;
            }
            return (C0Data)x[1];
        }
    }
    
    //todo ? :
    //    ?: : boolean, X, X → X
    
    //[(expression)], where (expression) must evaluate to an int
    
    class ForLoop extends Syntax{
                
        compl.etc.Method.C0Statement initialization;
        compl.etc.Method.C0Statement loop_guard;//should evaluate to boolean
        compl.etc.Method.C0Statement increment;//we don't need to know what this evaluates to
    }
    class WhileLoop extends Syntax{
        compl.etc.Method.C0Statement loop_guard;//should evaluate to boolean
    }
    class If extends Syntax{}
    class Else extends Syntax{}
    
    //evaluates an expression. an empty expression evaluates to void
    static <D extends ExpressionElement> D shuntingYard(Expression<D> exp){
        D val = null;
        for(ExpressionElement<D, D> e: exp.exp){
            if(e instanceof Expression){
                val = shuntingYard((Expression<D>)e);
            } else if (e instanceof ExpressionRelation){
                val = ((ExpressionRelation<D, D>)e).relation().get(e);
            } else {
                try{
                val = (D)e;
                }catch(Exception execp){
                    System.err.println("internal error: " + e + "was expected to ");
                }
            }
        }
        return val;
    }
    
    class Expression<D extends ExpressionElement> implements ExpressionElement<D, D>{
        //should be no arguments for just an expression
        public int numArgs(){return 0;}
        public D relate(D[] x){
            assert x.length == numArgs();
            return shuntingYard(this);
        }
        //an expression is a list of other expressions.
        List<ExpressionElement<D, D>> exp;
        //the tokens in an expression. Later used with the shunting yard 
        //algorithm to determine whether or not the expression is correct.
        
        //this method also allows us to evaluate syntax in the inner parentheses
        //before we evaluate the line as a whole (recursion!)
    } class C0Expression extends Expression<C0Data>{}
    //if there's a problem with a statement (or an expression)
    class C0Statement extends Thing{
        compl.data.FileManip text;//the original text source
        
        C0Expression exp;
        //math thing is defined as a string of relations which anlyze to other
        //values.
        //we test whether each operation works or not as we go.
    }

    //evaluate to a boolean
    class Assertion extends C0Statement{}

    class LoopInvariant extends Assertion{}

    class Requires extends Assertion{}
    class Ensures$ extends Assertion{}
    {
        int $$ = 0;
        $$++;
        
    }
}