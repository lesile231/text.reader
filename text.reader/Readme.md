
1. Producer - Consumer 패턴과 Broker 패턴을 추가해서 각각 Producer, Broker, Consumer의 결합은 느슨해지고, 응집도는 높아졌습니다.
2. Producer의 처리 로직(맨 앞 알파벳이 영문인지 확인, TextProcessingLogic 인터페이스)도 전략 패턴을 통해 분리해서 로직이 변경되거나 추가되기 쉽도록 하였습니다. 인터페이스를 통해 구조 수정에는 닫혀있고, 확장에는 열려있도록 하였습니다.
3. 현재는 Producer 1개, 파티션 수 : Consumer가 1:1로 되도록 해놓았지만, 각각 쓰레드가 더 추가 되어도 Thread-safe이 보장 되도록 임계 영역인 Broker의 partitionMap 인스턴스를 ConcurrentHashMap으로 구현하고, partitionMap안의 Collection은 Thread-safe과 함께 순차적으로 처리해야하므로 LinkedBlockingQueue로 구현하였습니다.
4. JDK 1.8 이상 버전을 사용하므로, try-with-resource 문 (JDK 1.7이상) 을 통해 FileReader와 FileWriter를 AutoClose 하였습니다.
5. 파티션은 설정값에 따라 파티션 수에 맞춰 Partition Id를 Key로 나누어주었고, Partition Id로는 a~z까지 아스키 코드를 파티션 수로 mod 연산한 값을 이용했습니다.
6. Heap 메모리 할당량에 따라 데이터를 지속적으로 읽어서 Queue에 담게 되면, 메모리 풀이 날수도 있으므로 Broker의 LinkedBlockingQueue 사이즈를 제한해두었습니다.
7. 처리가 끝나면, 각 Partition에 EOF 메시지를 보내 Consumer가 종료되도록 하였습니다.
8. null을 반환할 수도 있는 메소드에는 _Nullable이 붙어있습니다.