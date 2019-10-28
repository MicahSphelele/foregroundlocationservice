package sphelele.getuserlocation;


public class TestClass {

    public static void main(String [] args){

        System.out.println("Instance 1 : " + Single.getInstance().hashCode());
        System.out.println("Instance 2 : " + Single.getInstance().hashCode());


    }

   static class Single{
        private static Single instance=null;

        private Single(){

            //if(instance!=null){
                //throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
            //}
        }

         static  Single getInstance(){


            if(instance==null){
                instance = new Single();
            }

            return instance;
        }
    }
}
