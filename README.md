# MeuFutApp
Aplicativo para android para organizar peladas (futebol) / Android app made for organize informal soccer games (made exclusivilly for brazilians, wrote in pt-BR)

Descrição em pt-BR:
Este aplicativo foi feito para organizar peladas de futebol, tanto peladas avulsas quanto peladas semanais (em plano mensal).
Suas funcionalidades são:
  -Criação de diversas peladas (Futs), onde são setados:
    -Local do Fut
    -Preço a ser cobrado para Jogadores avulsos
    -Valor do aluguel da quadra
    -Se o Fut é ou não mensal (por meio de uma checkbox), caso o Fut seja mensal, aparecerão mais campos à serem preenchidos:
      -Preço a ser cobrado para Jogadores mensalistas
      -Dia da semana em que os Futs ocorrerão
      -Horário em que os Futs ocorrerão
      O usuário tem direito à criação de 1 Fut gratuito, após isto, deverá assistir um anúncio para liberar mais espaço.
      
  -Após criar um Fut, você pode acessar a "Página do Fut", onde o usuário pode:
    -Criar vários Jogadores, que serão os Jogadores mantidos na base de dados do Fut para poderem ser incluídos nos "Eventos" que serão discutidos mais à frente, em que serão     setados:
      -Nome do Jogador
      -Preço caso o Jogador participe como avulso (vem por padrão marcado na opcão "Valor padrão do Fut", que é o valor setado para o preço a ser cobrado para Jogadores avulsos, porém o usário tem a opção, caso deseje, de alterar este valor exclusivamente para o Jogador em quetão, um exemplo, seria setar o valor para R$0,00 para um goleiro por exemplo)
      -Preço caso o Jogador participe como mensalista (funcionamento semelhante ao anterior)
      -Se o Jogador é mensalista (caso seja ele já instanciará o Jogador como mensalista, caso não, como avulso
    O usuário tem direito à criação de 4 Jogadores gratuitos, após isto, deverá assistir um anúncio para liberar mais espaços.
    -Editar os Jogadores, pressionando sobre eles, ao entrar no formulário edição, também poderão ser observados os seguintes dados sobre o Jogador em questão:
      -Eventos presentes como avulso
      -Eventos presentes como mensalista
      -Calotes dados (vezes em que jogou e saiu sem pagar)
      -Vezes que furou (vezes em que confirmou presença para o usuário, porém no horário do Evento não compareceu
    -Observar dados sobre o Fut, são eles:
      -Local do Fut 
      -Dia da semana do Fut (apenas Futs mensais)
      -Tipo do Fut (avulso ou mensal)
      -Valor padrão avulso
      -Valor padrão mensalista (apenas Futs mensais)
      -Número de Jogadores cadastrados no Fut
      -Número de mensalistas cadastrados noFut (apenas Futs mensais)
      -Preço da quadra
      -Ganho com mensalistas (apenas Futs mensais)
      -Lucro/Prejuízo (sem contar os avulsos), este dado informa ao usuário se ele está, apenas com os mensalistas, lucrando ou tendo prejuízo, além de informar o valor desse lucro (em verde) ou prejuízo (em vermelho), para que o usuário tenha uma base de quantos Jogadores avulsos deverá convidar ao Fut para que se possa equilibrar as contas (apenas Futs mensais).
      -Possível ganho com os avulsos, esse dado informa o quanto pode se obter se forem convidados todos os avulsos para um Evento
    -Ao pressionar e segurar o dedo em qualquer Jogador, inicializa-se o modo de seleção de Jogadores, onde o usuário pode selecionar Jogadores para:
      -Copiar os Jogadores selecionados para outro Fut
      -Excluir os Jogadores selecionados da base de dados do Fut em questão
      -Adicionar os Jogadores selecionados à um Evento
      -Mudar status dos Jogadores selecionados (avulso/mensalista) (apenas Futs mensais)
    -Na barra de navegação:
      -Editar o Fut
      -Excluir o Fut da base de dados do App
      -Abrir os Eventos relacionados ao Fut em questão
  
  -Ao abrir os Eventos relacionados ao Fut, o usuário entrará na página "Meus Eventos", onde ele pode:
    -Adicionar novos Eventos:
      -Caso o Fut seja mensal, será aberto um diálogo, onde o usuário poderá escolher o mês referente ao mensal que o mesmo gostaria de iniciar, e o sistema criará automaticamente Eventos para cada dia da semana (escolhido no ato da criação do Fut) no mês solicitado. Estes Eventos serão interligados por um código de referência, que consiste do mês e ano do mensal (mm/yyyy ou m/yyyy), este código serve para que se possa observar dados referentes à todo o mensal em qualquer um dos Eventos, ou seja, o usuário poderá obter informações sobre o mensal como um todo em qualquer um dos Eventos que tenham seu código de referência.
      -Caso o Fut seja avulso, será aberto um diálogo onde usuário poderá escolher o dia e horário do Evento.
    O usuário tem direito à criação de 1 evento gratuito, após isto, deverá assistir um anúncio para liberar mais espaço.
    -Ao pressionar e segurar o dedo em qualquer Evento, inicializa-se o modo de seleção de Eventos, onde o usuário pode selecionar Eventos para:
      -Editar um Evento (disponível se apenas um Evento estiver selecionado, e se este evento estiver ativo)
      -Remover os Eventos selecionados da base de dados do Fut, esta opção removerá o Evento para sempre, ou seja, será como se o mesmo nunca tivesse existido, portanto, mesmo que previamente finalizado, não serão contabilizados os dados referentes aos Jogadores deste Evento, por exemplo: Caso um Jogador tenha deixado de pagar, e o Evento tenha sido finalizado, este Jogador receberia um acréscimo no campo "Calotes dados" (já descrito anteriormente neste guia), porém caso o Evento seja removido, mesmo que finalizado, esta estatística deixará de existir. Recomenda-se que seja feita uma reciclagem nos Eventos uma vez por ano, caso o aplicativo esteja apresentando lentidão ou esteja ocupando muito espaço na memória do celular.
      -Arquivar Evento (disponível apenas se todos os Eventos selecionados estiverem finalizados), esta opção serve para que o usuário possa esconder Eventos já finalizados, sem a necessidade de remove-los da base de dados. Caso o Evento já esteja arquivado, esta opção desarquivará o Evento.
      O usuário tem direito à 1 arquivamento gratuito, após isto, deverá assistir um anúncio para liberar mais espaço.
      -Finalizar Evento (disponível apenas se todos os Eventos selecionados estiverem ativos), esta opção serve para que o aplicativo possa computar os dados do Evento em questão para a atualização dos dados de cada Jogador participante no Evento, e para a atualização dos dados do mensal (se o Fut for mensal). Após finalizado, nenhuma modificação poderá ser mais feita no Evento
      -Finalizar e Arquivar Evento (disponível apenas se todos os Eventos estiverem ativos e não arquivados)
      -Exibir eventos arquivados
      -Entrar na "Página do Evento" pressionando sobre algum Evento.
      
  -Ao entrar na "Página do Evento", o usuário pode:
    -Adicionar Jogadores no Evento
    -Visualizar informações sobre o Evento:
      -Preço da quadra
      -Previsão de ganho com avulsos
      -Ganho obtido com avulsos
      -Jogadores no Evento
      -Quantidade de Jogadores que já pagaram
    -Visualizar informações sobre o mensal (apenas para Fut mensal):
      -Preço mensal da quadra
      -Total obtido com mensalistas
      -Total obtido com avulsos
      -Lucro/Prejuízo Total (Está escrito "Sem os avulsos" porém está sendo contabilizado os avulsos sim, irei arrumar este detalhe assim que o aplicativo completar a fase de publicação na PlayStore)
      -Nº de Jogadores diferentes que compareceram no mensal
      -Calotes recebidos no mensal
      -Vezes que furaram em algum Evento do mensal
    -Alternar o status do Jogador (Pago/NaoPago) pressionando sobre ele
    -Finalizar o Evento
    -Remover o Evento da base de dados do Fut
    -Ao pressionar e segurar o dedo em qualquer Jogador, inicializa-se o modo de seleção de Jogadores, onde o usuário pode selecionar Jogadores para:
      -Remover o Jogadores selecionados da base de dados do Evento
      -Marcar/Desmarcar Jogadores selecionados como faltantes ("furou")
      
 Esta foi a descrição completa do app MeuFut - Organizador de peladas (futebol), atualmente ele está em fase de aprovação na PlayStore.
 Caso queira falar comigo, seguem meus contatos:
  -Email 1: leodetoledo@outlook.com
  -Email 2: gigaklom@gmail.com
  -linkedin: https://www.linkedin.com/in/leonardo-neres-de-toledo-9756951a7/
