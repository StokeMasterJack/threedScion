package c3i.util.shared.futures;

public class Test {

    /*


        the idea is that instead of providing
        two handlers (onSuccess and onFailure)

        you just provide one

        Future<Person> f = executeAsync(bla).then( new OnSuccess(){

            void onSuccess(Future<Person> f){
                f.getResult(); //boom
            }
        }

     */

}
