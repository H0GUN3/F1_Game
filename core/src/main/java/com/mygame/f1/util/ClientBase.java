package com.mygame.f1.util;

import java.io.BufferedReader; // (서버 응답을 읽기 위해 추가 - 선택 사항)
import java.io.InputStreamReader; // (서버 응답을 읽기 위해 추가 - 선택 사항)
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 서버와 통신하는 클라이언트의 기본 클래스입니다.
 */
public class ClientBase {

    private Socket socket;
    private PrintWriter out;
    // private BufferedReader in; // (서버로부터 에코 응답을 받을 경우)

    public static float posX = 0, posY = 0;

    /**
     * 지정된 호스트와 포트로 서버에 연결합니다.
     * 성공 시 'out' (출력) 스트림을 초기화합니다.
     *
     * @param host 연결할 서버의 호스트 이름 또는 IP 주소
     * @param port 연결할 서버의 포트 번호
     */
    public void connect(String host, int port) {
        try {
            // 소켓을 생성하여 서버에 연결 시도
            this.socket = new Socket(host, port);

            // 서버로 데이터를 보내기 위한 'out' 스트림 초기화 (true: auto-flush)
            this.out = new PrintWriter(socket.getOutputStream(), true);

            /* // (선택 사항) 서버로부터 응답(에코)을 받기 위한 'in' 스트림
            this.in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            */

            System.out.println("서버에 연결되었습니다: " + host + ":" + port);

        } catch (UnknownHostException e) {
            System.err.println("알 수 없는 호스트입니다: " + host);
        } catch (IOException e) {
            System.err.println("서버 연결 중 I/O 오류 발생: " + e.getMessage());
        }
    }

    /**
     * "Hello" 문자열을 서버로 전송하는 테스트 함수입니다.
     * 이 함수가 호출되기 전에 'connect'가 성공적으로 완료되어
     * 'out' 스트림이 초기화되어 있어야 합니다.
     */
    public void sendTest() {
        if (this.out != null) {
            String message = "Hello";

            // "Hello" 문자열을 서버로 전송합니다.
            out.println(message);

            System.out.println("서버로 메시지 전송: " + message);

            /*
            // (선택 사항) 에코 서버의 응답을 받는 부분
            try {
                String response = in.readLine();
                System.out.println("서버로부터 에코: " + response);
            } catch (IOException e) {
                System.err.println("서버 응답 읽기 오류: " + e.getMessage());
            }
            */

        } else {
            // 'out'이 null일 경우 (연결이 안 된 경우)
            System.err.println("전송 실패: 서버에 연결되지 않았습니다.");
        }
    }

    /**
     * 서버와의 연결을 종료하고 모든 리소스를 닫습니다.
     */
    public void disconnect() {
        try {
            // 스트림과 소켓을 닫습니다. (null 체크 포함)
            // 'out'만 닫아도 기반이 되는 socket.getOutputStream()이 닫힙니다.
            if (out != null) out.close();
            // if (in != null) in.close(); // (in을 사용했다면)
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("서버와의 연결을 종료했습니다.");
            }
        } catch (IOException e) {
            System.err.println("연결 종료 중 오류 발생: " + e.getMessage());
        }
    }
}
