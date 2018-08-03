package nlab.practice.reactivetest

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * 변환 연산자에 대한 유닛 테스트
 *
 * 변환 연산자 : 변환 연사자는 데이터 흐름을 마음대로 변형할 수 있는 연산자들.
 *
 * @author Doohyun
 * @since 2018. 07. 20
 */
class ConvertExecutionUnitTest {

    /**
     * concatMap : 먼저 들어온 데이터 순서대로 처리를 보장하는 함수
     */
    @Test
    fun doConcatMap() {
        val numbers = arrayOf(1,3,5)

        Observable.interval(4, TimeUnit.SECONDS)
                .take(numbers.size.toLong())
                .map { numbers[it.toInt()] }
                .concatMap { number -> Observable.interval(2, TimeUnit.SECONDS).take(2).map { number } }
                .subscribe { println(it) }

        Thread.sleep(30000)
    }

    /**
     * flatMap : 순서를 보장하지는 않음.
     */
    @Test
    fun doFlatMap() {
        val numbers = arrayOf(1,3,5)

        Observable.interval(1, TimeUnit.SECONDS)
                .take(numbers.size.toLong())
                .map { numbers[it.toInt()] }
                .flatMap { number -> Observable.interval(6, TimeUnit.SECONDS).take(2).map { number } }
                .subscribe { println(it) }

        Thread.sleep(30000)
    }

    /**
     * SwitchMap : 순서 보장에 있어서 concatMap 과 비슷하지만, 순서를 맞추기 위한 중간 데이터는 중단하고 새 데이터를 실행.
     *
     * 마지막 데이터 실행은 보장
     */
    @Test
    fun doSwitchMap() {
        val numbers = arrayOf(1,3,5)

        Observable.interval(1, TimeUnit.SECONDS)
                .take(numbers.size.toLong())
                .map { numbers[it.toInt()] }
                .switchMap { number -> Observable.interval(6, TimeUnit.SECONDS).take(2).map { number } }
                .subscribe { println(it) }

        Thread.sleep(30000)
    }


    /**
     * Concat 형태 Single 데이터를 묶어서 처리
     *
     * 첫번째 Single 이 처리되고, 두번째가 처리됨이 보장
     */
    @Test
    fun doSingleOnConcatWith() {
        Single.fromCallable {
                    Thread.sleep(5000)
                    1
                }
                .concatWith { Single.just(5).subscribe(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.trampoline())
                .subscribe { println(it) }

        Thread.sleep(10000)
    }

    /**
     * Scan 은 reduce 와 기능이 비슷하지만, 중간 과정의 데이터를 Stream 으로 넘겨주는 형태
     */
    @Test
    fun doScan() {
        Observable.fromArray(1,2,3,4,5)
                .scan(0 ) {
                    n1, n2
                    ->
                    n1 + n2
                }
                .subscribe { println("누적계 테스트 : $it") }
    }

    /**
     * 2개 이상의 Observable 을 결합할 때 사용, 한쪽의 Observable 이 끝나지 않는다면 처리될 때까지 발행을 기다림
     *
     * -> folk & join library 와 비슷한 감이 있음.
     */
    @Test
    fun doZip() {
        // todo Zip 해야함.
    }
}