<template>
  <!--
  <label>Стерто <span id="drawPercent">{{ drawPercent }}%</span> площади</label>
  -->
  <div id="lotteryContainer" style="margin: 0 auto;max-width: 270px;">
    <div class="back-img">
      <img src="./img/cardBG.png" alt="" style="transform: rotate(-1deg);">
    </div>

    <canvas ref="backgroundCanvas" :style="{ position: 'absolute', left: '55px', top: '27px', pointerEvents: ticketsAvailable == 0 ? 'none' : 'auto' }"></canvas>
    <canvas ref="maskCanvas" :style="{ position: 'absolute', left: '55px', top: '27px', pointerEvents: ticketsAvailable == 0 ? 'none' : 'auto' }"></canvas>

    <div v-if="drawPercent > 50" class="lucky-message" :class="[isLuckyMessage ? 'luck' : 'unluck']">
      {{ isLuckyMessage ? 'СЧАСТЛИВЫЙ БИЛЕТ!' : 'Пусто. Попробуйте снова!' }}
    </div>

    <div @click="refreshTicket" class="card-block btn-update">
      <b style="width: 100%;">Обновить билет</b>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LotteryComponent',
  props: {
    ticketsAvailable: {
      type: Number,
      required: true
    },
    query: {
      type: String,
      required: true
    }
  },
  emits: ['lotteryEvent'],
  data() {
    return {
      width: 168,
      height: 53,
      cover: '#CCC',
      coverType: 'color',
      lottery: '',
      lotteryType: '',
      drawPercent: 0,
      isMouseDown: false,
      clientRect: null,
      lotteryReq: false,
      isLuckyMessage: false,
      showLuckyMessage: false,
      res: null,
      resSuccess: false,
    };
  },
  mounted() {
    this.init('', 'text');
  },
  methods: {
    createElement(tagName, attributes) {
      let ele = document.createElement(tagName);
      for (let key in attributes) {
        ele.setAttribute(key, attributes[key]);
      }
      return ele;
    },
    getTransparentPercent(ctx, width, height) {
      let imgData = ctx.getImageData(0, 0, width, height),
          pixles = imgData.data,
          transPixs = [];
      for (let i = 0, j = pixles.length; i < j; i += 4) {
        let a = pixles[i + 3];
        if (a < 128) {
          transPixs.push(i);
        }
      }
      return (transPixs.length / (pixles.length / 4) * 100).toFixed(2);
    },
    resizeCanvas(canvas, width, height) {
      canvas.width = width;
      canvas.height = height;
      const ctx = canvas.getContext('2d');
      ctx.clearRect(0, 0, width, height);
    },
    drawPoint(x, y) {
      const maskCanvas = this.$refs.maskCanvas;
      const maskCtx = maskCanvas.getContext('2d');
      maskCtx.beginPath();
      let radgrad = maskCtx.createRadialGradient(x, y, 0, x, y, 30);
      radgrad.addColorStop(0, 'rgba(0,0,0,0.6)');
      radgrad.addColorStop(1, 'rgba(255, 255, 255, 0)');
      maskCtx.fillStyle = radgrad;
      maskCtx.arc(x, y, 30, 0, Math.PI * 2, true);
      maskCtx.fill();
      this.drawPercent = this.getTransparentPercent(maskCtx, this.width, this.height);
      if (this.drawPercent > 10 && !this.lotteryReq) {
        this.lotteryReq = true;
        this.doLottery();
      } else if (this.drawPercent > 50 && this.resSuccess && this.lotteryReq) {
        this.resSuccess = false;
        this.$emit('lotteryEvent', this.res);
      }
    },
    bindEvent() {
      const canvas = this.$refs.maskCanvas;
      const device = (/android|webos|iphone|ipad|ipod|blackberry|iemobile|opera mini/i.test(navigator.userAgent.toLowerCase()));
      const clickEvtName = device ? 'touchstart' : 'mousedown';
      const moveEvtName = device ? 'touchmove' : 'mousemove';

      canvas.addEventListener(clickEvtName, (e) => {
        this.isMouseDown = true;
        let x = (device ? e.touches[0].clientX : e.clientX) - this.clientRect.left + document.documentElement.scrollLeft - document.documentElement.clientLeft;
        let y = (device ? e.touches[0].clientY : e.clientY) - this.clientRect.top + document.documentElement.scrollTop - document.documentElement.clientTop;
        this.drawPoint(x, y);
      });

      canvas.addEventListener(moveEvtName, (e) => {
        if (!device && !this.isMouseDown) {
          return false;
        }
        let x = (device ? e.touches[0].clientX : e.clientX) - this.clientRect.left + document.documentElement.scrollLeft - document.documentElement.clientLeft;
        let y = (device ? e.touches[0].clientY : e.clientY) - this.clientRect.top + document.documentElement.scrollTop - document.documentElement.clientTop;
        this.drawPoint(x, y);
      });

      document.addEventListener(device ? 'touchend' : 'mouseup', () => {
        this.isMouseDown = false;
      });
    },
    drawLottery() {
      const backgroundCanvas = this.$refs.backgroundCanvas;
      //const backCtx = backgroundCanvas.getContext('2d');

      if (!this.clientRect) {
        this.clientRect = backgroundCanvas.getBoundingClientRect();
      }

      if (this.lotteryType === 'text') {
        const textCanvas = backgroundCanvas;
        const textCtx = textCanvas.getContext('2d');
        this.resizeCanvas(textCanvas, this.width, this.height);
        textCtx.save();
        this.drawBackground(textCtx);
        this.drawText(textCtx, this.lottery)
        this.drawMask();
      }
    },
    drawBackground(textCtx) {
      textCtx.fillStyle = '#FFF';
      textCtx.fillRect(0, 0, this.width, this.height);
      textCtx.restore();
      textCtx.save();
    },
    drawText(textCtx, text, color, size=16) {
      // Заливаем канвас белым цветом
      textCtx.fillStyle = 'white';
      textCtx.fillRect(0, 0, this.width, this.height);
      let fontSize = size;
      textCtx.font = `Bold ${fontSize}px Arial`;
      textCtx.textAlign = 'center';
      textCtx.fillStyle = color;

      let lineHeight = fontSize * 1.2; // Высота строки

      // Разделяем текст на строки по символу \n
      const lines = text.split('\n');

      // Рисуем каждую строку отдельно
      lines.forEach((line, index) => {
        textCtx.fillText(line, this.width / 2, this.height / (2 * lines.length) + (index * lineHeight) + fontSize / 2);
      });

      textCtx.restore();
    },
    drawMask() {
      const maskCanvas = this.$refs.maskCanvas;
      const maskCtx = maskCanvas.getContext('2d');
      this.resizeCanvas(maskCanvas, this.width, this.height);

      // Заливка маски (цвет или изображение)
      if (this.coverType === 'color') {
        maskCtx.fillStyle = this.cover;
        maskCtx.fillRect(0, 0, this.width, this.height);
      } else if (this.coverType === 'image') {
        let coverImage = new Image();
        coverImage.onload = () => {
          maskCtx.drawImage(coverImage, 0, 0);
        };
        coverImage.src = this.cover;
      }

      // Добавление текста на маску
      maskCtx.font = 'Bold 12px Arial';
      maskCtx.fillStyle = '#000'; // Цвет текста
      maskCtx.textAlign = 'center';

      if (this.ticketsAvailable > 0) maskCtx.fillText('Сотрите защитный слой', this.width / 2, this.height / 2);
      else maskCtx.fillText('Нет доступных билетов', this.width / 2, this.height / 2);

      // Устанавливаем глобальную операцию для стирания
      maskCtx.globalCompositeOperation = 'destination-out';
    },
    init(lottery, lotteryType) {
      // Создать билет
      this.showLuckyMessage = false;
      this.lottery = lottery;
      this.lotteryType = lotteryType || 'image';
      this.drawLottery();
      this.bindEvent();
    },
    refreshTicket() {
      // Сбросить визуал билета
      this.lotteryReq = false;
      this.resSuccess = false;
      this.res = null;
      this.drawPercent = 0;
      this.init('', 'text');
    },
    doLottery() {
      // Не отправлять запрос если билетов нет
      if(this.ticketsAvailable < 1) return;

      // Получить канвас
      const textCtx = this.$refs.backgroundCanvas.getContext('2d');
      this.drawText(textCtx, 'Ожидайте...', '#000');

      // Получить результат лотереи
      const url = 'api/lotteries';
      fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Token': this.query
        }
      }).then(res => {
        if (!res.ok) throw new Error(`Ошибка ${res.status}: ${res.statusText}`);
        return res.json();
      }).then(res => {
        this.lottery = res.value;
        let text = `Билет ${res.order}\n${this.lottery} баллов`

        // Уменьшаем количество билетов
        this.$emit('ticket-decremented');

        // Обновить текст
        this.drawText(textCtx, text, '#F60');

        // Отобразить сообщение о выигрыше
        this.isLuckyMessage = this.lottery > 0;
        this.resSuccess = true;
        this.res = res;
      }).catch((error) => {
        this.drawText(textCtx, 'Проверьте\nинтернет соединение', '#ef503b', 14);
        console.error('Error doing lottery:', error);
      });
    }
  }
};
</script>

<style scoped>
body {
  height: 1000px;
}

#lotteryContainer {
  position: relative;
  width: auto;

}

#drawPercent {
  color: #F60;
}
</style>
