<script setup>
import MyLotery from './MyLotery.vue';
</script>

<template>
  <div v-if="!ModalActive">
    <div class="container">
      <div class="nav">
        <div class="nav-btn" @click="backHome()">
          <div class="nav-btn-img"><img src="./img/arrow.svg" alt=""></div>
          <div class="nav-btn-text">назад</div>
        </div>
      </div>
    </div>
    <div class="main-head">
      <div class="container2">
        Новогодняя Активность
      </div>
    </div>
    <div class="container">
      <div class="second-head">
        Собирайте награды, открывайте билеты и получайте баллы
      </div>
    </div>
    <div class="container">
      <div class="card-block">
        <div v-if="(currentValueFireworks >= maxValueFireworks) && (maxValueFireworks != 0) && !ModalActive" class="challenge-done">
          Поздравляем, вы собрали максимум фейерверков ({{ maxValueFireworks }} шт)!<br>
          Продолжайте открывать билеты, чтобы получить баллы и побороться за главный приз!
        </div>

        <div class="card">
          <div class="card-head">
            <div class="card-head-intro">
              <div class="card-head-img">
                <img src="./img/1.png" alt="">
              </div>
              <div class="card-head-text">
                Фейерверки
              </div>
            </div>
            <div class="card-head-info" @click="twoModalStart()">
              <img src="./img/777.svg" alt="">
            </div>
          </div>
          <div class="card-btn">
          </div>
          <div class="card-progressbar">
            <div class="card-progressbar-count">
              <div class="card-progressbar-count-min">{{ currentValueFireworks }}</div>
              <div class="card-progressbar-count-max">{{ maxValueFireworks }}</div>
            </div>
            <div class="card-progressbar-progressbar">
              <div class="card-progressbar-fill"
                   :style="{ width: ((currentValueFireworks / maxValueFireworks) * 100 > 100 ? 100 : (currentValueFireworks / maxValueFireworks) * 100) + '%' }"></div>
            </div>
          </div>
        </div>
      </div>
      <div class="card-block">
        <div v-if="(currentValueMandarin >= maxValueMandarin) && (maxValueMandarin != 0) && !ModalActive" class="challenge-done">
          Поздравляем, вы собрали максимум мандаринов ({{ maxValueMandarin }} шт)!<br>
          Продолжайте открывать билеты, чтобы получить баллы и побороться за главный приз!
        </div>

        <div class="card">
          <div class="card-head">
            <div class="card-head-intro">
              <div class="card-head-img">
                <img src="./img/2.png" alt="">
              </div>
              <div class="card-head-text">
                Мандарины
              </div>
            </div>
            <div class="card-head-info" @click="oneModalStart()">
              <img src="./img/777.svg" alt="">
            </div>
          </div>
          <div class="card-btn">
            <div v-if="!challengeAccepted" class="card-btn-btn bg-orange"
                :class="[challengeBtnTime ? 'btnTime' : '', challengeBtn ? 'btnchallenge' : '']" @click="challenge()">
              Принять вызов!
            </div>
            <div v-else class="card-btn-btn bg-green">
              Подтверждено!
            </div>
          </div>
          <div class="card-progressbar">
            <div class="card-progressbar-count">
              <div class="card-progressbar-count-min">{{ currentValueMandarin }}</div>
              <div class="card-progressbar-count-max">{{ maxValueMandarin }}</div>
            </div>
            <div class="card-progressbar-progressbar">
              <div class="card-progressbar-fill"
                   :style="{ width: ((currentValueMandarin / maxValueMandarin) * 100 > 100 ? 100 : (currentValueMandarin / maxValueMandarin) * 100) + '%' }"></div>
            </div>
            <div class="card-progressbar-data">
              дней до конца задания: {{ daysUntilExpiration }}
            </div>
          </div>
        </div>
      </div>
      <div class="bilet-block">
        <div class="bilet-block-head">Счастливые билеты</div>
        <div class="bilet-block-cards">
          <div class="bilet-block-card">
            <div class="bilet-block-card-head">Накоплено <br> билетов</div>
            <div class="bilet-block-card-count">{{ ticketsTotal }}</div>
          </div>
          <div class="bilet-block-card">
            <div class="bilet-block-card-head">Открыто <br> билетов</div>
            <div class="bilet-block-card-count">{{ ticketsOpened }}</div>
          </div>
        </div>
        <div class="bilet-block-ball">
          <div class="bilet-block-ball-text">
            Заработано <br> баллов
          </div>
          <div class="bilet-block-ball-count">
            {{ pointsTotal }}
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="main-btn-block" v-if="!ModalActive">
    <div class="main-btn" @click="ModalActiveStart()">Открыть билеты</div>
  </div>

  <div class="info-modal" v-if="oneModal">
    <div class="info-modal-back" @click="oneModalStart()"></div>
    <div class="info-modal-contant">
      <div class="info-modal-contant-section">
        <div class="info-modal-contant-section-head">
          <div>дополнительное задание</div>
          <div>ваш прогресс: <span v-html="progressString"></span></div>
        </div>
        <p class="info-modal-contant-section-text" v-html="houseData1"></p>
      </div>
      <div class="info-modal-contant-section info-modal-contant-section-two">
        <p class="info-modal-contant-section-text" v-html="houseData2"></p>
      </div>
      <div class="info-modal-contant-section">
        <p class="info-modal-contant-section-text"><b>Дата обновления: </b><span v-html="houseData4"></span></p>
      </div>
    </div>
  </div>
  <div class="info-modal" v-if="twoModal">
    <div class="info-modal-back" @click="twoModalStart()"></div>
    <div class="info-modal-contant">
      <div class="info-modal-contant-section">
        <!-- <div class="info-modal-contant-section-head">
          <div>дополнительное задание</div>
          <div>ваш прогресс: 2/3</div>
        </div> -->
        <div class="info-modal-contant-section-text">
          <p style="margin-bottom: 8px;">Получайте по одному фейерверку за:</p>

          <ul style="margin-bottom: 8px;">
            <li>- Каждые 10 корректных чеков;</li>
            <li>- Каждые 2 регистрации в Q Club;</li>
            <li>- Каждых 2 уникальных клиентов, которые воспользовались фишкой.</li>
          </ul>

          <p>Накопите 20 фейерверков и заработайте 2000 баллов!</p>
        </div>
      </div>
    </div>
  </div>
  <div class="second-modal" v-if="ModalActive">
    <div class="container">
      <div class="nav">
        <div class="nav-btn" @click="ModalActiveStart()">
          <div class="nav-btn-img"><img src="./img/arrow.svg" alt=""></div>
          <div class="nav-btn-text">назад</div>
        </div>
      </div>
    </div>
    <div class="info-modal-contant-section-head">
      <div>узнайте что скрывается</div>
      <div>за билетом</div>
    </div>
    <div class="second-head">
      доступно к открытию: {{ ticketsAvailable }}
    </div>
    <div class="container">
      <div class="card-block card-block-bilet">
        <div class="card" style=" height: 160px;">
          <div style="margin: 0 auto;">
            <MyLotery
            @ModalOff=ModalOff
            @lottery-event="lotteryHandler"
            @ticket-decremented="decrementTicket"
            :ticketsAvailable=ticketsAvailable
            :query=token
            />
          </div>
        </div>
      </div>
      <div class="card-block" style="text-align: center; padding: 16px 8px;" @click="accordionStart()">
        <b v-if="accordion" style="width: 100%">История участия</b>
        <div v-if="!accordion">
          <div class="accordion-head">
            <div>Номер билета</div>
            <div>|</div>
            <div>НОМИНАЛ</div>
          </div>
          <div class="accordion-content" v-for="(lottery) in lotteryHistory" :key="lottery.order"
               :class="{ 'text-green': lottery.value > 0 }">
            <div>{{ lottery.order }} билет</div>
            <div>{{ lottery.value }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Снежинки -->
  <div class="snow">
    <div class="snowflake"><img src="./img/snowflake.svg" alt="*" ></div>
    <div class="snowflake1"><img src="./img/snowflake.svg" alt="*" ></div>
    <div class="snowflake2"><img src="./img/snowflake.svg" alt="*" ></div>
    <div class="snowflake3"><img src="./img/snowflake.svg" alt="*" ></div>
    <div class="snowflake4"><img src="./img/snowflake.svg" alt="*" ></div>
    <div class="snowflake5"><img src="./img/snowflake.svg" alt="*" ></div>
    <div class="snowflake6"><img src="./img/snowflake.svg" alt="*" ></div>
    <div class="snowflake7"><img src="./img/snowflake.svg" alt="*" ></div>
    <div class="snowflake8"><img src="./img/snowflake.svg" alt="*" ></div>
    <div class="snowflake9"><img src="./img/snowflake.svg" alt="*" ></div>
    <div class="snowflake10"><img src="./img/snowflake.svg" alt="*" ></div>
    <div class="snowflake11"><img src="./img/snowflake.svg" alt="*" ></div>
    <div class="snowflake12"><img src="./img/snowflake.svg" alt="*" ></div>
    <div class="snowflake13"><img src="./img/snowflake.svg" alt="*" ></div>
    <div class="snowflake14"><img src="./img/snowflake.svg" alt="*" ></div>
  </div>

  <div class="bg-page">
    <img src="./img/bg_blur.jpg" alt="Фон">
  </div>
  <img src="./img/cardBG.png" style="display: none;">
</template>

<script>

export default {
  data() {
    return {
      token: "",
      dtprf: "",
      ticketsAvailable: 0,
      ticketsOpened: 0,
      ticketsTotal: 0,
      currentValueFireworks: 0,
      maxValueFireworks: 0,
      currentValueMandarin: 0,
      maxValueMandarin: 0,
      daysUntilExpiration: 0,
      progressString: '',
      oneModal: false,
      twoModal: false,
      challengeBtn: false,
      challengeBtnTime: false,
      challengeAccepted: false,
      accordion: true,
      ModalActive: false,
      data: null,
      lotteryHistory: [],
      pointsTotal: 0,
      house: null,
      houseData1: '',
      houseData2: '',
      houseData3: '',
      houseData4: '',
      houseData5: '',
    }
  },
  methods: {
    ModalWord(data) {
      this.ModalActive = data;
    },
    oneModalStart() {
      this.oneModal = !this.oneModal;
    },
    twoModalStart() {
      this.twoModal = !this.twoModal;
    },
    // Принять вызов
    challenge() {
      if (this.challengeAccepted) return;
      const url = `api/users/${this.dtprf}/markers`;
      fetch(url, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Token': this.token
        }
      }).then(response => {
        // Если пришло ОК
        if (response.ok) {
          this.challengeAccepted = response.ok;
        }
      }).catch((error) => {
        console.error('Error accepting challenge:', error);
        this.challengeAccepted = false;
      });

      this.challengeBtnTime = !this.challengeBtnTime;
      setTimeout(async () => {
        this.challengeBtn = !this.challengeBtn;
      }, 500);
    },
    accordionStart() {
      this.accordion = false
    },
    accordionEnd() {
      this.accordion = true
    },
    ModalActiveStart() {
      this.ModalActive = !this.ModalActive;
      this.accordionEnd()
    },
    // Распарсить JSON из разметки страницы
    parseJson() {

      // Прод
      const jsonEl = document.getElementById("json");
      this.data = JSON.parse(jsonEl.innerText);
      // Тест
      // this.data = JSON.parse('{"dtprf":"123","query":"JwJWamF432UmpbwPgp85rEV2jC+XaFoLaMhn7UC1NiW04NIEUD0mUTLaFJCyICmApUXJXJxsvoAvLqPbgJOz+4rc332IhCqblMOtRv+r3BdkxWoCgPZBPAtnuPMWVXfSt0W/cwTT/sQJTOFthlxnGq1rux8vG9F6oL1s1YFTytHqNyQ4Cn6AvYmXPw4CTrApz6gH9fZT0V9ZbjGfYHe2sdS8g4gZQjvtjGs4vEetxEI=","group":"OTHER","level":"LOW","ticketsAvail":75,"ticketsTotal":105,"mandarins":0,"fireworks":0,"challengeAccepted":true,"history":[{"order":1,"value":0},{"order":2,"value":0},{"order":3,"value":0},{"order":4,"value":0},{"order":5,"value":500},{"order":57,"value":0},{"order":58,"value":0},{"order":59,"value":0},{"order":60,"value":500},{"order":61,"value":0},{"order":62,"value":0},{"order":63,"value":0},{"order":64,"value":0},{"order":65,"value":500},{"order":66,"value":0},{"order":67,"value":0},{"order":68,"value":0},{"order":69,"value":0},{"order":70,"value":500},{"order":71,"value":0},{"order":72,"value":0},{"order":73,"value":0},{"order":74,"value":0},{"order":75,"value":500},{"order":76,"value":0},{"order":77,"value":0},{"order":78,"value":0},{"order":79,"value":0},{"order":80,"value":500},{"order":81,"value":0}],"house":{"name":"САЛОН АПГРЕЙД","description":"Сейчас задание в этом доме недоступно. Обратитесь к своему торговому представителю ФМСМ, чтобы добавить задание на следующий период.","tasksComplete":0,"tasksTotal":0,"maxCoins":0,"caption":"","tasks":[],"buttons":[]}}');
      this.token = this.data.query;
      this.dtprf = this.data.dtprf;
      this.currentValueFireworks = this.data.fireworks > 0 ? this.data.fireworks : 0;
      this.maxValueFireworks = this.data.fireworksMax;
      this.currentValueMandarin = this.data.mandarins > 0 ? this.data.mandarins : 0;
      this.maxValueMandarin = this.data.mandarinsMax;
      this.challengeAccepted = this.data.challengeAccepted;
      this.progressString = this.data.progressString;
      this.ticketsTotal = this.data.ticketsTotal;
      this.ticketsAvailable = this.data.ticketsAvail;
      this.ticketsOpened = this.data.ticketsTotal - this.data.ticketsAvail;
      this.daysUntilExpiration = this.daysUntil(this.data.markerExpiringOn);
      this.lotteryHistory = this.data.history;
      this.data.history.forEach(e => {
        this.pointsTotal += e.value;
      });
      this.houseData1 = this.data.house.description;
      this.houseData2 = this.data.house.tasks[0]?.length === 0 ? '' : this.data.house.tasks[0]?.description;
      this.houseData3 = this.data.house.tasks[1]?.length === 0 ? '' : this.data.house.tasks[1]?.description;
      this.houseData4 = this.data.house.caption;
      this.houseData5 = '';
    },
    daysUntil(date) {
      // Parse the provided date string
      const [day, month, year] = date.split('.').map(Number);
      const providedDate = new Date(year, month - 1, day);

      // Get today's date
      const today = new Date();
      today.setHours(0, 0, 0, 0); // Set time to midnight to avoid partial days

      // Calculate the difference in time
      const timeDiff = providedDate - today;

      // Convert time difference to days
      let days = Math.ceil(timeDiff / (1000 * 60 * 60 * 24));
      return days > 0 ? days : 0;
    },
    backHome() {
      // Переход домой через форму
      let targetUrl = '/townofgames/play';
      let form = document.createElement('form');
      form.setAttribute('action', targetUrl);
      form.setAttribute('method', 'POST');

      let queryInput = document.createElement('input');
      queryInput.setAttribute('type', 'hidden');
      queryInput.setAttribute('name', 'query');
      queryInput.setAttribute('value', this.token);
      form.appendChild(queryInput);

      document.body.appendChild(form);
      form.submit();
    },
    lotteryHandler(res) {
      this.addHistoryEntry(res);
      this.addPoints(res.value);
    },
    addPoints(points) {
      this.pointsTotal += points;
    },
    decrementTicket() {
      this.ticketsAvailable--;
      this.ticketsOpened++;
    },
    addHistoryEntry(entry) {
      this.lotteryHistory.push(entry);
    }
  },
  mounted() {
    this.parseJson();
  },
}
</script>

<style src="./main.css">

</style>
