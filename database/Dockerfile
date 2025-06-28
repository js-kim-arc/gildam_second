FROM mysql:8.0

# 타임존 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# (선택) UTF-8 인코딩 설정
ENV LANG=C.UTF-8